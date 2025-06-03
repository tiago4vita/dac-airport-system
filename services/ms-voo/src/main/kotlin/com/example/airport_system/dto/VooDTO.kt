package com.example.airport_system.ms_voo.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class VooDTO(
    val codigo: String,
    val data: LocalDateTime,
    val estado: String,
    val valorPassagem: BigDecimal,
    val quantidadePoltTotal: Int,
    val quantidadePoltOcup: Int,
    val aeroportoOrigem: AeroportoDTO,
    val aeroportoDestino: AeroportoDTO,
) {
    // Métodos de validação
    fun isMesmoDia(outro: VooDTO): Boolean {
        return this.data.toLocalDate().isEqual(outro.data.toLocalDate())
    }

    fun horarioFormatado(): String {
        return "${data.hour.toString().padStart(2, '0')}:${data.minute.toString().padStart(2, '0')}"
    }

    //fix currency func
    fun toCurrencyString(): String {
        return "R$ ${valorPassagem.setScale(2)}"
    }
}