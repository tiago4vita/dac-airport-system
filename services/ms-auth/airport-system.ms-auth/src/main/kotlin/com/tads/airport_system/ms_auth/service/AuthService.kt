package com.tads.airport_system.ms_auth.service

import com.tads.airport_system.ms_auth.repository.UsuarioRepository

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository
) {
    fun authenticate(login: String, senha: String): Boolean {
        val usuario = usuarioRepository.findByEmail(login)
        
        if (usuario == null) {
            println("User not found: $login")
            return false
        }
        
        if (!usuario.ativo) {
            println("User is inactive: $login")
            return false
        }
        
        if (usuario.senha != senha) {
            println("Password mismatch for user: $login")
            return false
        }
        
        println("User authenticated successfully: $login")
        return true
    }
} 