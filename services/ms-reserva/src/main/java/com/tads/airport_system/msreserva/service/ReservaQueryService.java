package com.tads.airport_system.msreserva.service;

import com.tads.airport_system.msreserva.model.ReservaView;
import com.tads.airport_system.msreserva.repository.ReservaViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for handling query operations on the query database.
 * This service is part of the CQRS pattern and handles all read operations.
 */
@Service
@Transactional(transactionManager = "queryTransactionManager", readOnly = true)
public class ReservaQueryService {

    private final ReservaViewRepository reservaViewRepository;

    @Autowired
    public ReservaQueryService(ReservaViewRepository reservaViewRepository) {
        this.reservaViewRepository = reservaViewRepository;
    }

    /**
     * Find a reservation by ID
     * @param id the reservation ID
     * @return Optional containing the reservation view if found
     */
    public Optional<ReservaView> findById(String id) {
        return reservaViewRepository.findById(id);
    }

    /**
     * Find all reservations for a specific flight
     * @param vooId the flight ID
     * @return list of reservation views
     */
    public List<ReservaView> findByVooId(String vooId) {
        return reservaViewRepository.findByVooId(vooId);
    }

    /**
     * Find all reservations
     * @return list of all reservation views
     */
    public List<ReservaView> findAll() {
        return reservaViewRepository.findAll();
    }

    /**
     * Check if a reservation exists
     * @param id the reservation ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(String id) {
        return reservaViewRepository.existsById(id);
    }
} 