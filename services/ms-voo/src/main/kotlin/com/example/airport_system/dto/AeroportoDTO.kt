package com.example.airport_system.ms_voo.dto

import java.math.BigDecimal
import java.time.LocalDateTime
    

data class AeroportoDTO(
    val codigo: String,
    val nome: String,
    val cidade: String,
    val estado: String
)