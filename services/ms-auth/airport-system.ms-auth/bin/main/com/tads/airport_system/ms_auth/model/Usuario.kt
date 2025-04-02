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

    @Column(unique = true, nullable = false)
    @Email(message = "Email inválido")
    var email: String,
    @Column(nullable = false)
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    val senha: String,
    val tipoUser: userTipe,
    val ativo: Boolean



) {

    enum class userTipe{ADMIN,USUARIO,CLIENTE}

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