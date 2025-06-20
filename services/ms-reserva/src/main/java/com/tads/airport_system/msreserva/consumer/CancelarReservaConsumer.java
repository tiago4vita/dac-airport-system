package com.tads.airport_system.msreserva.consumer;

import com.tads.airport_system.msreserva.model.Reserva;
import com.tads.airport_system.msreserva.model.EstadoReserva;
import com.tads.airport_system.msreserva.model.AlteracaoEstadoReserva;
import com.tads.airport_system.msreserva.dto.ReservaDTO;
import com.tads.airport_system.msreserva.repository.ReservaRepository;
import com.tads.airport_system.msreserva.repository.EstadoReservaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Component
public class CancelarReservaConsumer {
    private final ReservaRepository reservaRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CancelarReservaConsumer(
            ReservaRepository reservaRepository,
            EstadoReservaRepository estadoReservaRepository,
            ObjectMapper objectMapper,
            RabbitTemplate rabbitTemplate) {
        this.reservaRepository = reservaRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.cancelar")
    @Transactional
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            // Deserializa a mensagem pra conseguir o ID da reserva
            String reservaId = objectMapper.readValue(msg, String.class);

            // Find the reservation by ID
            Optional<Reserva> optionalReserva = reservaRepository.findById(reservaId);

            if (optionalReserva.isPresent()) {
                Reserva reserva = optionalReserva.get();
                EstadoReserva estadoAtual = reserva.getEstado();

                // checa se a reserva pode ser cancelada (esta no estado CRIADA ou CHECK-IN)
                if (estadoAtual.getCodigoEstado().equals(EstadoReserva.Estado.CRIADA.name()) || 
                    estadoAtual.getCodigoEstado().equals(EstadoReserva.Estado.CHECK_IN.name())) {

                    // Pega o estado CANCELADA
                    EstadoReserva estadoCancelada = estadoReservaRepository.findByCodigoEstado(EstadoReserva.Estado.CANCELADA.name());

                    // Atualiza o estado da reserva para CANCELADA e registra a mudança de estado
                    AlteracaoEstadoReserva alteracao = reserva.atualizarEstado(estadoCancelada);

                    // Retorna milhas pro saldo do cliente e registra que elas vieram de um cancelamento
                    // nota: envolver chamada de serviço ou atualizar entidade
                    // mudar depois, deixar no log por enquanto
                    System.out.println("Milhas retornadas para o cliente da reserva: " + reservaId);
                    System.out.println("Registro de milhas de cancelamento criado para a reserva: " + reservaId);

                    // salva a reserva atualizada
                    reservaRepository.save(reserva);

                    // resposta de sucesso
                    response.put("success", true);
                    response.put("message", "Reserva cancelada com sucesso");
                    response.put("reserva", new ReservaDTO(
                            reserva.getId(),
                            reserva.getVooId(),
                            reserva.getDataHoraRes(),
                            reserva.getEstado()
                    ));

                    System.out.println("Reserva cancelada via RabbitMQ: (" + reserva.getId() + ")");
                } else {
                    // reserva não pode ser cancelada
                    response.put("success", false);
                    response.put("message", "Reserva não pode ser cancelada. Estado atual: " + estadoAtual.getDescricao());
                    System.out.println("Tentativa de cancelar reserva em estado inválido: " + estadoAtual.getCodigoEstado());
                }
            } else {
                // reserva não encontrada
                response.put("success", false);
                response.put("message", "Reserva não encontrada");
                System.out.println("Tentativa de cancelar reserva inexistente: " + reservaId);
            }
        } catch (Exception e) {
            // lida com qualquer erro
            response.put("success", false);
            response.put("message", "Erro ao processar cancelamento: " + e.getMessage());
            System.err.println("Erro ao processar cancelamento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Mandar mensagem de resposta se necessario
            // poderia ser mandado pra fila de resposta ou diretamente pra uma troca especifica
            // mudar depois, deixar no log por enquanto
            try {
                System.out.println("Resposta: " + objectMapper.writeValueAsString(response));
            } catch (JsonProcessingException e) {
                System.err.println("Erro ao serializar resposta: " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> buscarReservaPorId(String id) {
        return reservaRepository.findById(id);
    }
}
