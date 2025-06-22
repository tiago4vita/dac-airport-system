package com.tads.airport_system.msreserva.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue reservaEfetuarQueue() { return new Queue("reserva.efetuar", true); }

    @Bean
    public Queue reservaCancelarQueue() { return new Queue("reserva.cancelar", true); }

    @Bean
    public Queue reservaCheckInQueue() { return new Queue("reserva.checkin", true); }

    @Bean
    public Queue reservaConsultarQueue() { return new Queue("reserva.consultar", true); }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    @Bean
    public Queue reservaEventosQueue() {
        return new Queue("reserva.eventos", true);
    }
}
