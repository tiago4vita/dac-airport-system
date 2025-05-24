package com.tads.airport_system.ms_auth.dto

import com.tads.airport_system.ms_auth.model.Usuario

data class LoginResultDTO(
    val success: Boolean,
    val usuario: Usuario? = null,
    val message: String? = null
) 