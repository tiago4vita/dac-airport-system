package airportsystem.msvoo.services;

import airportsystem.msvoo.model.Voo;
import airportsystem.msvoo.model.Aeroporto;
import airportsystem.msvoo.model.Reserva;
import airportsystem.msvoo.dto.UpdateVooDTO;
import airportsystem.msvoo.enums.StatusVoos;
import airportsystem.msvoo.repository.VooRepository;
import airportsystem.msvoo.repository.AeroportoRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VooService {
    private static final Logger logger = LoggerFactory.getLogger(VooService.class);
    
    @Autowired
    private VooRepository vooRepository;

    @Autowired
    private AeroportoRepository aeroportoRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<Voo> findByOrigemAndDestino(String origemCodigo, String destinoCodigo) {
        return vooRepository.findByOrigemCodigoAndDestinoCodigo(origemCodigo, destinoCodigo);
    }

    public List<Voo> listarTodosVoos() {
        return vooRepository.findAllWithOrigemAndDestino();
    }
    
    public Voo listarUmVoo(Long id) {
        return vooRepository.findByIdWithOrigemAndDestino(id)
            .orElseThrow(() -> new EntityNotFoundException("Voo não encontrado: " + id));         
    }

    @Transactional
    public Voo inserirVoo(Voo voo, String origemCodigo, String destinoCodigo) {
        Aeroporto origem = aeroportoRepository.findByCodigo(origemCodigo)
            .orElseThrow(() -> new EntityNotFoundException("Aeroporto de origem não encontrado: " + origemCodigo));
        
        Aeroporto destino = aeroportoRepository.findByCodigo(destinoCodigo)
            .orElseThrow(() -> new EntityNotFoundException("Aeroporto de destino não encontrado: " + destinoCodigo));
        
        voo.setOrigem(origem);
        voo.setDestino(destino);

        return vooRepository.save(voo);
    }

    @Transactional
    public Voo editarVoo(Long id, UpdateVooDTO dto) {
        Voo vooExistente = vooRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Voo não encontrado: " + id));

        if (dto.getDataHoraPartida() != null) {
            vooExistente.setDataHoraPartida(dto.getDataHoraPartida());
        }
        if (dto.getCodigoOrigem() != null) {
            vooExistente.setOrigem(aeroportoRepository.findByCodigo(dto.getCodigoOrigem())
                .orElseThrow(() -> new EntityNotFoundException("Aeroporto de origem não encontrado: " + dto.getCodigoOrigem())));
        }
        if (dto.getCodigoDestino() != null) {
            vooExistente.setDestino(aeroportoRepository.findByCodigo(dto.getCodigoDestino())
                .orElseThrow(() -> new EntityNotFoundException("Aeroporto de destino não encontrado: " + dto.getCodigoDestino())));
        }
        if (dto.getValorPassagem() != null) {
            vooExistente.setValorPassagem(dto.getValorPassagem());
        }
        if (dto.getQuantidadeAssentos() != null) {
            vooExistente.setQuantidadeAssentos(dto.getQuantidadeAssentos());
        }
        if (dto.getQuantidadePassageiros() != null) {
            vooExistente.setQuantidadePassageiros(dto.getQuantidadePassageiros());
        }
        if (dto.getStatus() != null) {
            validarStatus(dto.getStatus());
            vooExistente.setStatus(dto.getStatus());
        }

        return vooRepository.save(vooExistente);
    }

    @Transactional
    public void atualizarStatus(Long id, String statusStr) {
        logger.info("Atualizando status do voo {} para {}", id, statusStr);
        
        StatusVoos status;
        try {
            status = StatusVoos.fromDescricao(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + statusStr);
        }
        
        Voo voo = vooRepository.findByIdWithReservas(id)
            .orElseThrow(() -> new EntityNotFoundException("Voo não encontrado: " + id));

        logger.debug("Voo encontrado: ID={}, Código={}", voo.getId(), voo.getCodigoVoo());
        logger.debug("ReservasTracking inicializado: {}", voo.getReservasTracking() != null);
        logger.debug("Número de reservas: {}", 
            voo.getReservasTracking() != null ? voo.getReservasTracking().size() : 0);
        
        if (voo.getReservasTracking() != null && !voo.getReservasTracking().isEmpty()) {
            voo.getReservasTracking().forEach(rt -> 
                logger.debug("Reserva encontrada - ID: {}, Status: {}, Quantidade: {}", 
                    rt.getReservaId(), rt.getStatus(), rt.getQuantidade())
            );
        }

        if (!isValidStatusTransition(voo.getStatusEnum(), status)) { 
            throw new IllegalStateException(
                "Transição de status inválida: " + voo.getStatus() + " -> " + status.getDescricao());
        }

        validarStatus(status);
        voo.setStatus(status);
        vooRepository.save(voo);
        
        if (status == StatusVoos.CANCELADO) {
            List<Long> reservaIds = voo.getReservasTracking().stream()
                .filter(rt -> !"CANCELADA".equals(rt.getStatus()))  
                .map(Reserva::getReservaId)
                .collect(Collectors.toList());
                    
            logger.info("Encontradas {} reservas ativas para cancelamento no voo {}", 
                reservaIds.size(), id);
                
            if (!reservaIds.isEmpty()) {
                Map<String, Object> sagaPayload = new HashMap<>();
                sagaPayload.put("tipo", "VOO_CANCELADO");
                sagaPayload.put("vooId", id);
                sagaPayload.put("reservaIds", reservaIds);
                
                logger.info("Iniciando saga para cancelamento das reservas do voo {}", id);
                rabbitTemplate.convertAndSend("saga-exchange", "voo.status.request", sagaPayload);
            } else {
                logger.info("Nenhuma reserva ativa encontrada para cancelar no voo {}", id);
            }
        }  else if (status == StatusVoos.REALIZADO) {
            List<Long> reservaIds = voo.getReservasTracking().stream()
                    .filter(rt -> !"CANCELADA".equals(rt.getStatus()))  
                    .map(Reserva::getReservaId)
                    .collect(Collectors.toList());
                        
                if (!reservaIds.isEmpty()) {
                    Map<String, Object> sagaPayload = new HashMap<>();
                    sagaPayload.put("tipo", "VOO_REALIZADO");
                    sagaPayload.put("vooId", id);
                    sagaPayload.put("reservaIds", reservaIds);
                    
                    logger.info("Iniciando saga para atualização das reservas do voo realizado {}", id);
                    rabbitTemplate.convertAndSend("saga-exchange", "voo.status.request", sagaPayload);
                }
            }
        
        logger.info("Status do voo {} atualizado com sucesso para {}", id, status.getDescricao());
    }

   
    @Transactional
    public boolean adicionarReserva(Long vooId, Reserva tracking) {
        try {
            Voo voo = vooRepository.findById(vooId)
                .orElseThrow(() -> new EntityNotFoundException("Voo não encontrado: " + vooId));

            if (!voo.hasAvailableSeats(tracking.getQuantidade())) {
                logger.error("Não há assentos suficientes disponíveis no voo {}", vooId);
                return false;
            }

            voo.getReservasTracking().add(tracking);
            voo.updatePassengerCount();
            
            vooRepository.save(voo);
            
            logger.info("Successfully added reservation tracking for voo {} with {} seats", 
                       vooId, tracking.getQuantidade());
            return true;

        } catch (Exception e) {
            logger.error("Error adding reservation tracking to voo {}: {}", vooId, e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean atualizarReservasTracking(Long vooId, List<Reserva> trackingUpdates) {
        try {
            Voo voo = vooRepository.findById(vooId)
                .orElseThrow(() -> new EntityNotFoundException("Voo não encontrado: " + vooId));

            for (Reserva update : trackingUpdates) {
                Optional<Reserva> existingTracking = voo.getReservasTracking().stream()
                    .filter(t -> t.getReservaId().equals(update.getReservaId()))
                    .findFirst();

                if (existingTracking.isPresent()) {
                    Reserva tracking = existingTracking.get();
                    tracking.setStatus(update.getStatus());
                    tracking.setQuantidade(update.getQuantidade());
                } else {
                    voo.getReservasTracking().add(update);
                }
            }

            voo.updatePassengerCount();
            vooRepository.save(voo);
            
            logger.info("Successfully updated {} reservation trackings for voo {}", 
                       trackingUpdates.size(), vooId);
            return true;

        } catch (Exception e) {
            logger.error("Error updating reservation trackings for voo {}: {}", vooId, e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean updateReservaStatus(Long vooId, Long reservaId, String newStatus) {
        try {
            Voo voo = vooRepository.findById(vooId)
                .orElseThrow(() -> new EntityNotFoundException("Voo não encontrado: " + vooId));

            Reserva tracking = voo.getReservasTracking().stream()
                .filter(r -> r.getReservaId().equals(reservaId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reserva tracking não encontrado"));

            tracking.setStatus(newStatus);
            voo.updatePassengerCount();
            
            vooRepository.save(voo);
            return true;

        } catch (Exception e) {
            logger.error("Error updating reservation status for voo {} reserva {}: {}", 
                        vooId, reservaId, e.getMessage());
            return false;
        }
    }
    private boolean isValidStatusTransition(StatusVoos currentStatus, StatusVoos newStatus) {
        if (currentStatus == null) return true;
        
        switch (currentStatus) {
            case CONFIRMADO:
                return newStatus == StatusVoos.REALIZADO || newStatus == StatusVoos.CANCELADO;
            case REALIZADO:
                return false;
            case CANCELADO:
                return false;
            default:
                return true;
        }
    }

    private void validarStatus(StatusVoos status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
    }
}
