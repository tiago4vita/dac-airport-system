package com.tads.airport_system.msreserva.consumer;

import com.tads.airport_system.msreserva.model.ReservaView;
import com.tads.airport_system.msreserva.dto.ReservaDTO;
import com.tads.airport_system.msreserva.service.ReservaQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Component
public class ConsultarReservaConsumer {
    private final ReservaQueryService reservaQueryService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ConsultarReservaConsumer(ReservaQueryService reservaQueryService, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.reservaQueryService = reservaQueryService;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.consultar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            String reservaCodigo = objectMapper.readValue(msg, String.class);

            // In CQRS pattern, we query the read model (ReservaView) instead of the command model
            Optional<ReservaView> reservaView = reservaQueryService.findById(reservaCodigo);

            if (reservaView.isPresent()) {
                ReservaView view = reservaView.get();
                System.out.println("Reserva consultada via RabbitMQ (Query DB): (" + view.getId() + ") " + msg);

                response.put("success", true);
                response.put("reserva", new ReservaDTO(
                    view.getId(),
                    view.getVooId(),
                    null, // clienteId not available in view
                    view.getDataHoraRes(),
                    view.getValor(),
                    view.getQuantidadePoltronas(),
                    view.getMilhasUtilizadas(),
                    createEstadoReservaFromView(view)
                ));
            } else {
                response.put("success", false);
                response.put("message", "Reserva não encontrada: " + reservaCodigo);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao consultar reserva: " + e.getMessage());
            System.err.println("Erro ao consultar reserva: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                String responseJson = objectMapper.writeValueAsString(response);
                rabbitTemplate.convertAndSend("retorno", responseJson);
                System.out.println("Resposta de consulta enviada: " + responseJson);
            } catch (JsonProcessingException e) {
                System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            }
        }
    }
    
    /**
     * Create EstadoReserva object from ReservaView data
     * This is needed to maintain compatibility with existing DTO structure
     */
    private com.tads.airport_system.msreserva.model.EstadoReserva createEstadoReservaFromView(ReservaView view) {
        com.tads.airport_system.msreserva.model.EstadoReserva estado = new com.tads.airport_system.msreserva.model.EstadoReserva();
        estado.setCodigoEstado(view.getEstadoCodigo());
        estado.setSigla(view.getEstadoSigla());
        estado.setDescricao(view.getEstadoDescricao());
        return estado;
    }
}
