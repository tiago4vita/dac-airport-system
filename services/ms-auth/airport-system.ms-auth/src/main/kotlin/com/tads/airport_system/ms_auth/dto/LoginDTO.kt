package com.tads.airport_system.ms_auth.dto

import jakarta.validation.constraints.NotBlank

data class LoginDTO(
    @field:NotBlank(message = "Email é obrigatório")
    val email: String,

    @field:NotBlank(message = "Senha é obrigatória")
    val senha: String
) 