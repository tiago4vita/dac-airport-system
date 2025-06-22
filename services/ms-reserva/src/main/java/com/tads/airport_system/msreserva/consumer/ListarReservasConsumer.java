package com.tads.airport_system.msreserva.consumer;

import com.tads.airport_system.msreserva.model.Reserva;
import com.tads.airport_system.msreserva.dto.ReservaDTO;
import com.tads.airport_system.msreserva.repository.ReservaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class ListarReservasConsumer {
    private final ReservaRepository reservaRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ListarReservasConsumer(ReservaRepository reservaRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.reservaRepository = reservaRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.listar")
    public String receiveMessage(String clienteId) throws JsonProcessingException {
        System.out.println("ListarReservasConsumer received message for clienteId: '" + clienteId + "'");

        List<Reserva> reservas = reservaRepository.findByClienteId(clienteId);
        System.out.println("Reservas listadas via RabbitMQ para cliente: " + clienteId + " - Total: " + reservas.size());

        ObjectNode response = objectMapper.createObjectNode();
        ArrayNode reservasJson = objectMapper.valueToTree(reservas);
        response.put("success", true);
        response.put("total", reservas.size());
        response.set("reservas", reservasJson);
        String jsonResponse = objectMapper.writeValueAsString(response);

        System.out.println("Sending response: " + jsonResponse);
        return jsonResponse;
    }
}
