package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, String> {
    
    EstadoReserva findByCodigoEstado(String codigoEstado);
    
    EstadoReserva findBySigla(String sigla);
    
    boolean existsByCodigoEstado(String codigoEstado);
}