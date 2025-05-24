package com.tads.airport_system.ms_reserva.model

import jakarta.persistence.*

@Entity
@Table(name = "reserva")
data class Reserva(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(nullable = false)
    val flightId: Long,

    @Column(nullable = false)
    val dataReserva: String, // Data da reserva

    @Column(nullable = false)
    val status: String // Estado da reserva
) {
    override fun toString(): String {
        return "Reserva(id=$id, flightId=$flightId, dataReserva=$dataReserva, status='$status')"
    }
}