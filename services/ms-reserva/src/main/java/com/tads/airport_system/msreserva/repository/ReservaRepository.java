package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository for Reserva entities in the command database.
 * This repository is used EXCLUSIVELY for write operations (commands) in the CQRS pattern.
 * All read operations should use ReservaViewRepository instead.
 */
@Repository
@Transactional(transactionManager = "commandTransactionManager")
public interface ReservaRepository extends JpaRepository<Reserva, String> {
    // This repository only handles write operations (commands)
    // All queries should be done through ReservaViewRepository
    
    /**
     * Find all reservations by client ID
     * This is a special case where we need client ID information not available in the view
     * @param clienteId the client ID
     * @return list of reservations for the client
     */
    List<Reserva> findByClienteId(String clienteId);
}