package com.tads.airport_system.ms_reserva.dto
import com.tads.airport_system.ms_reserva.model.Reserva

data class ReservaDTO(
    val id: Long,
    val vooId: Long, // ID do Voo no microsserviço voo
    val dataReserva: String,
    val status: String // Status da reserva (ex: "confirmada", "cancelada")
) {
    companion object {
        fun fromModel(reserva: Reserva): ReservaDTO {
            return ReservaDTO(
                id = reserva.id,
                vooId = reserva.vooId,
                dataReserva = reserva.dataReserva,
                status = reserva.status
            )
        }
    }
}