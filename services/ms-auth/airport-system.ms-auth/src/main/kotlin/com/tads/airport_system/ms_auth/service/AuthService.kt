package com.tads.airport_system.ms_auth.service

import com.tads.airport_system.ms_auth.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository
) {
    fun authenticate(email: String, senha: String): Boolean {
        val usuario = usuarioRepository.findByEmail(email)
        
        if (usuario == null) {
            println("User not found: $email")
            return false
        }
        
        if (!usuario.ativo) {
            println("User is inactive: $email")
            return false
        }
        
        if (usuario.senha != senha) {
            println("Password mismatch for user: $email")
            return false
        }
        
        return true
    }
} 