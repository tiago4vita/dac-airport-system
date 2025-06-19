package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String>{

    Reserva findByID(String id);

    boolean existsById(String id);
}