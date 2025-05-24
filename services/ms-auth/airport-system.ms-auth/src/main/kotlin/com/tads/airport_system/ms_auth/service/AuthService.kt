package com.tads.airport_system.ms_auth.service

import com.tads.airport_system.ms_auth.dto.LoginResultDTO
import com.tads.airport_system.ms_auth.model.Usuario
import com.tads.airport_system.ms_auth.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)
    
    fun authenticate(login: String, senha: String): Boolean {
        val usuario = usuarioRepository.findByEmail(login)
        
        if (usuario == null) {
            logger.info("User not found: $login")
            return false
        }
        
        if (!usuario.ativo) {
            logger.info("User is inactive: $login")
            return false
        }
        
        if (usuario.senha != senha) {
            logger.info("Password mismatch for user: $login")
            return false
        }
        
        logger.info("User authenticated successfully: $login")
        return true
    }
} 