package com.tads.airport_system.msreserva.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tads.airport_system.msreserva.dto.EfetuarReservaDTO;
import com.tads.airport_system.msreserva.event.ReservaEvent;
import com.tads.airport_system.msreserva.model.Reserva;
import com.tads.airport_system.msreserva.service.ReservaCommandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class EfetuarReservaConsumer {

    private final ReservaCommandService reservaCommandService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EfetuarReservaConsumer(ReservaCommandService reservaCommandService, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.reservaCommandService = reservaCommandService;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.efetuar")
    public void receiveMessage(String msg) throws JsonMappingException,JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            EfetuarReservaDTO efetuarReservaDTO = objectMapper.readValue(msg, EfetuarReservaDTO.class);
            Reserva novaReserva = createReserva(efetuarReservaDTO);
            System.out.println("Reserva efetuada com sucesso: (" + novaReserva + ") " + msg);

            response.put("success", true);
            response.put("reserva", novaReserva);
            response.put("message", "Reserva efetuada com sucesso");

        } catch (ResponseStatusException e) {
            System.err.println("Erro ao efetuar reserva: " + e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errorType", "VALIDATION_ERROR");
            response.put("statusCode", e.getStatusCode().value());

        } catch (Exception e) {
            System.err.println("Erro ao processar imagem: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Erro interno: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }

        //mandar mensagem pra fila de retorno
        try {
            String responseJson = objectMapper.writeValueAsString(response);
            rabbitTemplate.convertAndSend("retorno", responseJson);
            System.out.println("Mensagem enviada com sucesso para a fila de retorno: " + responseJson);

        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
        }
    }

    @Transactional(transactionManager = "commandTransactionManager")
    public Reserva createReserva(EfetuarReservaDTO efetuarReservaDTO) {
        // Use the command service to create the reservation
        Reserva savedReserva = reservaCommandService.createReserva(
            efetuarReservaDTO.getCodigo_voo(),
            efetuarReservaDTO.getCodigo_cliente()
        );

        try {
            ReservaEvent event = new ReservaEvent("RESERVA_CRIADA", savedReserva);
            rabbitTemplate.convertAndSend("reserva.eventos", objectMapper.writeValueAsString(event));
            System.out.println("Evento de reserva criada enviado: " + event);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter evento para JSON: " + e.getMessage());
        }

        return savedReserva;
    }
}