package com.tads.airport_system.ms_auth.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.slf4j.LoggerFactory

@Service
class ClienteService(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(ClienteService::class.java)
    
    @Value("\${ms-cliente.url:http://ms-cliente:8080}")
    private lateinit var msClienteUrl: String
    
    fun getClienteDetailsByEmail(email: String): Map<String, Any>? {
        try {
            logger.info("Fetching cliente details for email: $email")
            val response = restTemplate.getForObject(
                "$msClienteUrl/api/clientes/by-email/$email", 
                Map::class.java
            )
            
            logger.info("Received cliente details: $response")
            return response
        } catch (e: Exception) {
            logger.error("Error fetching cliente details: ${e.message}", e)
            return null
        }
    }
} 