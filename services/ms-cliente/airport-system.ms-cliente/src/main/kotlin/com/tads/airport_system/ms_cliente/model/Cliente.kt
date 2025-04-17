package com.tads.airport_system.ms_cliente.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.br.CPF
import java.time.LocalDateTime

@Entity
@Table(name = "clientes")
data class Cliente(
    @Id
    @CPF(message = "CPF inválido")
    @Column(length = 11)
    val cpf: String,

    @field:NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    var nome: String,

    @field:Email(message = "Email inválido")
    @field:NotBlank(message = "Email é obrigatório")
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var rua: String,

    @Column(nullable = false)
    var numero: String,

    @Column
    var complemento: String?,

    @Column(length = 8, nullable = false)
    var cep: String,

    @Column(nullable = false)
    var cidade: String,

    @Column(length = 2, nullable = false)
    var uf: String,

    @Column(nullable = false)
    var milhas: Long = 0,

    @Column(name = "data_criacao", nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now(),

    @Column(name = "data_atualizacao")
    var dataAtualizacao: LocalDateTime? = null
) 