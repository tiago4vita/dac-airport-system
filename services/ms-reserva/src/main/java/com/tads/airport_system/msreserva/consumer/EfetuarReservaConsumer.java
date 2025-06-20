package com.tads.airport_system.msreserva.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tads.airport_system.msreserva.dto.ReservaDTO;
import com.tads.airport_system.msreserva.event.ReservaEvent;
import com.tads.airport_system.msreserva.model.Reserva;
import com.tads.airport_system.msreserva.repository.ReservaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class EfetuarReservaConsumer {

    private final ReservaRepository reservaRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EfetuarReservaConsumer(ReservaRepository reservaRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.reservaRepository = reservaRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.efetuar")
    public void receiveMessage(String msg) throws JsonMappingException,JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            ReservaDTO reservaDTO = objectMapper.readValue(msg, ReservaDTO.class);
            Reserva novaReserva = createReserva(reservaDTO);
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

    @Transactional
    public Reserva createReserva(ReservaDTO reservaDTO) {
        if (reservaRepository.existsById(reservaDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Reserva " + reservaDTO.getId() + " já existe");
        }

        Reserva reserva = new Reserva(
            reservaDTO.getId(),
            reservaDTO.getVooId(),
            reservaDTO.getEstado(),
            reservaDTO.getDataHoraRes()
        );

        Reserva savedReserva = reservaRepository.save(reserva);

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
