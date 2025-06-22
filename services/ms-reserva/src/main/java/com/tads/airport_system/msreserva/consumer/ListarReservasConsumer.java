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
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            String clienteId = objectMapper.readValue(msg, String.class);

            // Find all reservations for the client
            List<Reserva> reservas = reservaRepository.findByClienteId(clienteId);

            System.out.println("Reservas listadas via RabbitMQ para cliente: " + clienteId + " - Total: " + reservas.size());

            // Convert to DTOs
            List<ReservaDTO> reservasDTO = reservas.stream()
                .map(reserva -> new ReservaDTO(
                    reserva.getId(),
                    reserva.getVooId(),
                    reserva.getClienteId(),
                    reserva.getDataHoraRes(),
                    reserva.getValor(),
                    reserva.getQuantidadePoltronas(),
                    reserva.getMilhasUtilizadas(),
                    reserva.getEstado()
                ))
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("reservas", reservasDTO);
            response.put("total", reservasDTO.size());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao listar reservas: " + e.getMessage());
            System.err.println("Erro ao listar reservas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                String responseJson = objectMapper.writeValueAsString(response);
                rabbitTemplate.convertAndSend("retorno", responseJson);
                System.out.println("Resposta de listagem enviada: " + responseJson);
            } catch (JsonProcessingException e) {
                System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            }
        }
    }
}
