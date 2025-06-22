package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.ReservaView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for ReservaView entities in the query database.
 * This repository is used EXCLUSIVELY for read operations (queries) in the CQRS pattern.
 * All write operations should use ReservaRepository instead.
 */
@Repository
@Transactional(transactionManager = "queryTransactionManager", readOnly = true)
public interface ReservaViewRepository extends JpaRepository<ReservaView, String> {
    
    /**
     * Find a reservation view by id
     * @param id the reservation id
     * @return the reservation view
     */
    ReservaView findByIdEquals(String id);
    
    /**
     * Find reservation views by flight id
     * @param vooId the flight id
     * @return the reservation views
     */
    java.util.List<ReservaView> findByVooId(String vooId);
    
    /**
     * Check if a reservation view exists by id
     * @param id the reservation id
     * @return true if exists, false otherwise
     */
    boolean existsById(String id);
    
    /**
     * Find all reservation views
     * @return list of all reservation views
     */
    java.util.List<ReservaView> findAll();
}