package com.tads.airport_system.ms_auth.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

@Document(collection = "usuarios")
data class Usuario(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    @field:Email(message = "Email inválido")
    var email: String,

    @field:Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    val senha: String,
    
    val tipoUser: UserType,
    val ativo: Boolean = true
) {
    enum class UserType { ADMIN, USUARIO, CLIENTE }

    fun isEmailValid(email: String): Boolean {
        // Verifica se o email é nulo ou vazio
        if (email.isBlank()) return false

        // Verifica se tem apenas um @
        if (email.count { it == '@' } != 1) return false

        // Divide em partes locais e de domínio
        val parts = email.split('@')
        val localPart = parts[0]
        val domainPart = parts[1]

        // Verifica partes vazias
        if (localPart.isEmpty() || domainPart.isEmpty()) return false

        // Verifica se o domínio tem pelo menos um ponto
        if ('.' !in domainPart) return false

        // Verifica se o domínio termina com extensão válida
        val domainExtensions = listOf("com", "org", "net", "br", "io", "co")
        val lastDotIndex = domainPart.lastIndexOf('.')
        val extension = domainPart.substring(lastDotIndex + 1)
        if (extension.length < 2 || !domainExtensions.contains(extension.lowercase())) {
            return false
        }

        // Regex mais preciso
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }

    fun ativar() = copy(ativo = true)
    fun desativar() = copy(ativo = false)
}