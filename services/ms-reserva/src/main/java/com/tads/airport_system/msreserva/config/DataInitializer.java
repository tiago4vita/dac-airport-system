package com.tads.airport_system.msreserva.config;

import com.tads.airport_system.msreserva.model.EstadoReserva;
import com.tads.airport_system.msreserva.repository.EstadoReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EstadoReservaRepository estadoReservaRepository;

    @Autowired
    public DataInitializer(EstadoReservaRepository estadoReservaRepository) {
        this.estadoReservaRepository = estadoReservaRepository;
    }

    @Override
    public void run(String... args) {
        // funcao que inicializa os estados de reserva pre-definidos se eles nao existirem
        for (EstadoReserva.Estado estado : EstadoReserva.Estado.values()) {
            if (!estadoReservaRepository.existsByCodigoEstado(estado.name())) {
                EstadoReserva estadoReserva = EstadoReserva.fromEnum(estado);
                estadoReservaRepository.save(estadoReserva);
            }
        }
    }
}