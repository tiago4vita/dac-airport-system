package com.tads.airport_system.msreserva.consumer;

import com.tads.airport_system.msreserva.model.Reserva;
import com.tads.airport_system.msreserva.model.EstadoReserva;
import com.tads.airport_system.msreserva.dto.ReservaDTO;
import com.tads.airport_system.msreserva.event.ReservaEvent;
import com.tads.airport_system.msreserva.service.ReservaCommandService;
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
public class EmbarcarReservaConsumer {
    private final ReservaCommandService reservaCommandService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EmbarcarReservaConsumer(
            ReservaCommandService reservaCommandService,
            ObjectMapper objectMapper,
            RabbitTemplate rabbitTemplate) {
        this.reservaCommandService = reservaCommandService;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.embarcar")
    @Transactional(transactionManager = "commandTransactionManager")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            // Deserializa a mensagem pra conseguir o ID da reserva
            String reservaId = objectMapper.readValue(msg, String.class);

            // Use the command service to board the passenger
            Optional<Reserva> optionalReserva = reservaCommandService.embarcarPassageiro(reservaId);

            if (optionalReserva.isPresent()) {
                Reserva reserva = optionalReserva.get();

                // Publica evento para atualizar o modelo de consulta (CQRS)
                try {
                    ReservaEvent event = new ReservaEvent("RESERVA_EMBARCADA", reserva);
                    rabbitTemplate.convertAndSend("reserva.eventos", objectMapper.writeValueAsString(event));
                    System.out.println("Evento de reserva embarcada enviado: " + event);
                } catch (JsonProcessingException e) {
                    System.err.println("Erro ao converter evento para JSON: " + e.getMessage());
                }

                // resposta de sucesso
                response.put("success", true);
                response.put("message", "Passageiro embarcado com sucesso");
                response.put("reserva", new ReservaDTO(
                        reserva.getId(),
                        reserva.getVooId(),
                        reserva.getClienteId(),
                        reserva.getDataHoraRes(),
                        reserva.getValor(),
                        reserva.getQuantidadePoltronas(),
                        reserva.getMilhasUtilizadas(),
                        reserva.getEstado()
                ));

                System.out.println("Passageiro embarcado via RabbitMQ: (" + reserva.getId() + ")");
            } else {
                // reserva não encontrada ou não pode ser embarcada
                response.put("success", false);
                response.put("message", "Reserva não encontrada ou não pode ser embarcada (deve estar em CHECK-IN)");
                System.out.println("Tentativa de embarcar reserva inexistente ou inválida: " + reservaId);
            }
        } catch (Exception e) {
            // lida com qualquer erro
            response.put("success", false);
            response.put("message", "Erro ao processar embarque: " + e.getMessage());
            System.err.println("Erro ao processar embarque: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Envia resposta via RabbitMQ
            try {
                String responseJson = objectMapper.writeValueAsString(response);
                rabbitTemplate.convertAndSend("retorno", responseJson);
                System.out.println("Resposta de embarque enviada: " + responseJson);
            } catch (JsonProcessingException e) {
                System.err.println("Erro ao serializar resposta: " + e.getMessage());
            }
        }
    }
}
