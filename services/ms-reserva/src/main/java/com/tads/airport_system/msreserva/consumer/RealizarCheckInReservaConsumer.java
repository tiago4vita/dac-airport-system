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
public class RealizarCheckInReservaConsumer {
    private final ReservaCommandService reservaCommandService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RealizarCheckInReservaConsumer(
            ReservaCommandService reservaCommandService,
            ObjectMapper objectMapper,
            RabbitTemplate rabbitTemplate) {
        this.reservaCommandService = reservaCommandService;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.checkin")
    @Transactional(transactionManager = "commandTransactionManager")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            // Deserializa a mensagem pra conseguir o ID da reserva
            String reservaId = objectMapper.readValue(msg, String.class);

            // Use the command service to perform check-in
            Optional<Reserva> optionalReserva = reservaCommandService.realizarCheckIn(reservaId);

            if (optionalReserva.isPresent()) {
                Reserva reserva = optionalReserva.get();

                // Publica evento para atualizar o modelo de consulta (CQRS)
                try {
                    ReservaEvent event = new ReservaEvent("RESERVA_CHECK_IN", reserva);
                    rabbitTemplate.convertAndSend("reserva.eventos", objectMapper.writeValueAsString(event));
                    System.out.println("Evento de check-in realizado enviado: " + event);
                } catch (JsonProcessingException e) {
                    System.err.println("Erro ao converter evento para JSON: " + e.getMessage());
                }

                // resposta de sucesso
                response.put("success", true);
                response.put("message", "Check-in realizado com sucesso");
                response.put("reserva", new ReservaDTO(
                        reserva.getId(),
                        reserva.getClienteId(),
                        reserva.getVooId(),
                        reserva.getDataHoraRes(),
                        reserva.getEstado()
                ));

                System.out.println("Check-in realizado via RabbitMQ: (" + reserva.getId() + ")");
            } else {
                // reserva não encontrada ou não pode ser feita check-in
                response.put("success", false);
                response.put("message", "Reserva não encontrada ou não pode ser feita check-in (verifique se não está cancelada)");
                System.out.println("Tentativa de check-in em reserva inexistente ou inválida: " + reservaId);
            }
        } catch (Exception e) {
            // lida com qualquer erro
            response.put("success", false);
            response.put("message", "Erro ao processar check-in: " + e.getMessage());
            System.err.println("Erro ao processar check-in: " + e.getMessage());
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
}
