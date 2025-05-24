package com.tads.airport_system..ms_reserva.service

import com.tads.airport_system.ms_reserva.repository.ReservaRepository
import com.tads.airport_system.ms_reserva.dto.ReservaDTO
import org.springframework.stereotype.Service
import com.tads.airport_system.ms_reserva.model.Reserva

@Service
class ReservaService(private val reservaRepository: ReservaRepository) {

    fun createReserva(reservaDTO: ReservaDTO): ReservaDTO {
        val reserva = Reserva(
            userId = reservaDTO.userId,
            flightId = reservaDTO.flightId,
            seatNumber = reservaDTO.seatNumber,
            status = reservaDTO.status
        )
        val savedReserva = reservaRepository.save(reserva)
        return ReservaDTO.fromModel(savedReserva)
    }

    fun getReservaById(id: Long): ReservaDTO? {
        val reserva = reservaRepository.findById(id).orElse(null)
        return reserva?.let { ReservaDTO.fromModel(it) }
    }

    fun getAllReservas(): List<ReservaDTO> {
        return reservaRepository.findAll().map { ReservaDTO.fromModel(it) }
    }