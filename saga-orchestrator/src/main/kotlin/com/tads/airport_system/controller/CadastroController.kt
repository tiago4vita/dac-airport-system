package com.tads.airport_system.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import com.tads.airport_system.rabbitmq.sagas.Saga  // Adjust the package path as needed
import com.tads.airport_system.model.ClienteCadastro  // Adjust the package path as needed

@RestController
class CadastroController(    
    private val saga: Saga
) {
    @PostMapping("/cadastro")
    fun cadastrarCliente(@RequestBody clienteCadastro: ClienteCadastro): ResponseEntity<String> {
        return try {
            val response = saga.executeSaga(clienteCadastro)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: ${e.message}")
        }
    }
}