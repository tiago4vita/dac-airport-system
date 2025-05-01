package com.tads.airport_system.ms_auth.service

import com.tads.airport_system.ms_auth.model.Usuario
import com.tads.airport_system.ms_auth.repository.UsuarioRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class RabbitMQConsumer(
    private val usuarioRepository: UsuarioRepository
) {
    private val logger = LoggerFactory.getLogger(RabbitMQConsumer::class.java)
    
    @RabbitListener(queues = ["auth-service-queue"])
    fun receiveMessage(message: Map<String, Any>) {
        try {
            logger.info("Received message: $message")
            
            val action = message["action"] as String
            val payload = message["payload"] as Map<String, Any>
            
            when (action) {
                "CREATE_USER" -> createUser(payload)
                else -> logger.warn("Unknown action: $action")
            }
        } catch (e: Exception) {
            logger.error("Error processing message: ${e.message}", e)
        }
    }
    
    private fun createUser(payload: Map<String, Any>) {
        val email = payload["email"] as String
        val senha = payload["senha"] as String
        val tipo = Usuario.TipoUsuario.valueOf(payload["tipo"] as String)
        val ativo = payload["ativo"] as Boolean
        
        if (usuarioRepository.existsByEmail(email)) {
            logger.warn("User with email $email already exists")
            return
        }
        
        val usuario = Usuario(
            email = email,
            senha = senha,
            tipo = tipo,
            ativo = ativo
        )
        
        usuarioRepository.save(usuario)
        logger.info("User created: $email")
    }
} 