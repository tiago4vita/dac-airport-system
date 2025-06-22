package com.tads.airport_system.msreserva.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tads.airport_system.msreserva.event.ReservaEvent;
import com.tads.airport_system.msreserva.model.ReservaView;
import com.tads.airport_system.msreserva.repository.ReservaViewRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler de eventos para eventos de reserva.
 * Esse componente é responsável por atualizar o BD de leitura baseado nos eventos do BD de operações.
 * Parte fundamental do padrão CQRS pedido pelo professor. Basicamente assegura que o modelo de leitura (desnormalizado)
 * fique a par com o modelo de operações (normalizado).
 */
@Component
public class ReservaEventHandler {

    private final ReservaViewRepository reservaViewRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReservaEventHandler(ReservaViewRepository reservaViewRepository, ObjectMapper objectMapper) {
        this.reservaViewRepository = reservaViewRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Lida com os eventos de reserva da fila reserva.eventos
     * @param eventMessage a mensagem de evento serializada
     */
    @RabbitListener(queues = "reserva.eventos")
    public void handleReservaEvent(String eventMessage) {
        try {
            ReservaEvent event = objectMapper.readValue(eventMessage, ReservaEvent.class);
            System.out.println("Evento recebido: " + event);
            
            switch (event.getTipo()) {
                case "RESERVA_CRIADA":
                    handleReservaCriada(event);
                    break;
                case "RESERVA_CANCELADA":
                    handleReservaCancelada(event);
                    break;
                case "RESERVA_ESTADO_ALTERADO":
                    handleReservaEstadoAlterado(event);
                    break;
                default:
                    System.out.println("Tipo desconhecido de evento: " + event.getTipo());
            }
        } catch (JsonProcessingException e) {
            System.err.println("Erro deserializando evento: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro lidando com evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lida com os eventos de RESERVA_CRIADA
     * @param event o evento
     */
    @Transactional(transactionManager = "queryTransactionManager")
    public void handleReservaCriada(ReservaEvent event) {
        ReservaView reservaView = new ReservaView(
            event.getReservaId(),
            event.getVooId(),
            event.getDataHoraRes(),
            event.getEstadoCodigo(),
            event.getEstadoSigla(),
            event.getEstadoDescricao()
        );
        
        reservaViewRepository.save(reservaView);
        System.out.println("ReservaView criada: " + reservaView);
    }

    /**
     * Lida com eventos de RESERVA_CANCELADA
     * @param event o evento
     */
    @Transactional(transactionManager = "queryTransactionManager")
    public void handleReservaCancelada(ReservaEvent event) {
        ReservaView reservaView = reservaViewRepository.findById(event.getReservaId())
            .orElse(null);
            
        if (reservaView != null) {
            reservaView.setEstadoCodigo(event.getEstadoCodigo());
            reservaView.setEstadoSigla(event.getEstadoSigla());
            reservaView.setEstadoDescricao(event.getEstadoDescricao());
            reservaViewRepository.save(reservaView);
            System.out.println("ReservaView atualizada (cancelada): " + reservaView);
        } else {
            System.err.println("ReservaView não encontrada para cancelamento: " + event.getReservaId());
        }
    }

    /**
     * Lida com os eventos de RESERVA_ESTADO_ALTERADO
     * @param event o evento
     */
    @Transactional(transactionManager = "queryTransactionManager")
    public void handleReservaEstadoAlterado(ReservaEvent event) {
        ReservaView reservaView = reservaViewRepository.findById(event.getReservaId())
            .orElse(null);
            
        if (reservaView != null) {
            reservaView.setEstadoCodigo(event.getEstadoCodigo());
            reservaView.setEstadoSigla(event.getEstadoSigla());
            reservaView.setEstadoDescricao(event.getEstadoDescricao());
            reservaViewRepository.save(reservaView);
            System.out.println("ReservaView atualizada (estado da reserva mudou): " + reservaView);
        } else {
            System.err.println("ReservaView não encontrada para mudança do estado da reserva: " + event.getReservaId());
        }
    }
}