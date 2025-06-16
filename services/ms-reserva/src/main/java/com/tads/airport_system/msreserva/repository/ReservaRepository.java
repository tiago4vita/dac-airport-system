package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, String>{

    Reserva findById(String id);

    boolean existsById(String id);
}