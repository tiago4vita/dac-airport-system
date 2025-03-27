package com.example.meuprojeto.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    val cpf: String,
    val nome: String,
    val CEP: String,
    val email: String,
    val senha: String,
    val dataCriacao: LocalDateTime = LocalDateTime.now(),
    val milhas: Int = 0,
    
    val ativo: Boolean = true,
    val tipoUsuario: String = "COMUM"
) {

    fun ativar() = copy(ativo = true)
    fun desativar() = copy(ativo = false)
}