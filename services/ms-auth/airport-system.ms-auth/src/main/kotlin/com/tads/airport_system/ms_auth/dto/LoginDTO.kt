package com.tads.airport_system.ms_auth.dto

import jakarta.validation.constraints.NotBlank

data class LoginDTO(
    @field:NotBlank(message = "Login é obrigatório")
    val login: String,

    @field:NotBlank(message = "Senha é obrigatória")
    val senha: String
) 