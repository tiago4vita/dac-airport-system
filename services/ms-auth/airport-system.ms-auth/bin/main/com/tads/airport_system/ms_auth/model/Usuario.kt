package com.tads.airport_system.ms_auth.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Document(collection = "usuarios")
data class Usuario(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    @field:Email(message = "Email inválido")
    @field:NotBlank(message = "Email não pode ser vazio")
    val email: String,

    @field:NotBlank(message = "Senha não pode ser vazia")
    val senha: String, // Stores SHA256+SALT hash from API Gateway

    val tipo: TipoUsuario,
    
    val ativo: Boolean = true
) {
    enum class TipoUsuario {
        CLIENTE, FUNCIONARIO
    }

    fun isEmailValid(email: String): Boolean {
        if (email.isBlank()) return false
        if (email.count { it == '@' } != 1) return false

        val parts = email.split('@')
        val localPart = parts[0]
        val domainPart = parts[1]

        if (localPart.isEmpty() || domainPart.isEmpty()) return false
        if ('.' !in domainPart) return false

        val domainExtensions = listOf("com", "org", "net", "br", "io", "co")
        val lastDotIndex = domainPart.lastIndexOf('.')
        val extension = domainPart.substring(lastDotIndex + 1)
        
        if (extension.length < 2 || !domainExtensions.contains(extension.lowercase())) {
            return false
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }

    fun ativar() = copy(ativo = true)
    fun desativar() = copy(ativo = false)
}