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

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Component
public class ConsultarReservaConsumer {
    private final ReservaRepository reservaRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ConsultarReservaConsumer(ReservaRepository reservaRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.reservaRepository = reservaRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "reserva.consultar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            String reservaCodigo = objectMapper.readValue(msg, String.class);

            // Em uma implementação CQRS mais completa, deveríamos consultar o modelo de leitura (ReservaView)
            // em vez do modelo de comando (Reserva), mas isso exigiria mudanças grandes demais
            Optional<Reserva> reserva = buscarReservaPorId(reservaCodigo);

            if (reserva.isPresent()) {
                Reserva reservaConsultada = reserva.get();
                System.out.println("Reserva consultada via RabbitMQ: (" + reservaConsultada.getId() + ") " + msg);

                response.put("success", true);
                response.put("reserva", new ReservaDTO(
                    reservaConsultada.getId(),
                    reservaConsultada.getVooId(),
                    reservaConsultada.getClienteId(),
                    reservaConsultada.getDataHoraRes(),
                    reservaConsultada.getEstado()
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

    @Transactional(readOnly = true)
    public Optional<Reserva> buscarReservaPorId(String id){
        return reservaRepository.findById(id);
    }
}
