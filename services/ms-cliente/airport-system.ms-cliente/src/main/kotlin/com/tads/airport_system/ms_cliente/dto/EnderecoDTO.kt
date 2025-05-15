package com.tads.airport_system.ms_cliente.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class EnderecoDTO(
    @field:NotBlank(message = "CEP é obrigatório")
    @field:Size(min = 8, max = 8, message = "CEP deve ter 8 dígitos")
    val cep: String,

    @field:NotBlank(message = "UF é obrigatória")
    val uf: String,

    @field:NotBlank(message = "Cidade é obrigatória")
    val cidade: String,

    @field:NotBlank(message = "Bairro é obrigatório")
    val bairro: String,

    @field:NotBlank(message = "Rua é obrigatória")
    val rua: String,

    @field:NotBlank(message = "Número é obrigatório")
    val numero: String,

    val complemento: String?
) 