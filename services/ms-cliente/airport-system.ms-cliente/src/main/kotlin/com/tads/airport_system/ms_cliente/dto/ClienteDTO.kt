package com.tads.airport_system.ms_cliente.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.br.CPF

data class ClienteDTO(
    @field:CPF(message = "CPF inválido")
    val cpf: String,

    @field:Email(message = "Email inválido")
    @field:NotBlank(message = "Email é obrigatório")
    val email: String,

    @field:NotBlank(message = "Nome é obrigatório")
    val nome: String,

    val saldo_milhas: Long = 0,

    @field:Valid
    val endereco: EnderecoDTO
) 