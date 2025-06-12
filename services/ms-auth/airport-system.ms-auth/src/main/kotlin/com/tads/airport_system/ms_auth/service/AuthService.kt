package com.tads.airport_system.ms_auth.service

import com.tads.airport_system.ms_auth.repository.UsuarioRepository
import org.springframework.stereotype.Service
import com.tads.airport_system.ms_auth.model.Usuario
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.core.DirectExchange

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    @Qualifier//
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

        val routingkey = "cliente" //criar verificação da role

        val userData: DTO = runBlocking{
            val request = async { asyncSendAndReceive("auth",routingkey,it.code.toString())}
            val response = request.await()
        }

        println("User authenticated successfully: $login")
        return true
    }

    private suspend fun asyncSendAndReceive(
        exchange: DirectExchange,
        routingkey: String,
        message: String
    ): String {
        return withContext(Dispatchers.IO) {
            rabbitTemplate.convertSendAndReceive(exchange, routingkey, message) as String
        }

    }
} 