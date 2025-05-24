package com.tads.airport_system.ms_reserva.repository

import com.tads.airport_system.ms_reserva.model.Reserva
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservaRepository : JpaRepository<Reserva, Long> {
    fun findByUserId(userId: Long): List<Reserva>
    fun findByFlightId(flightId: Long): List<Reserva>
    fun findBySeatNumber(seatNumber: String): List<Reserva>
    fun findByStatus(status: String): List<Reserva>
}