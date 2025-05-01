package com.tads.airport_system.ms_cliente.controller

import com.tads.airport_system.ms_cliente.dto.ClienteDTO
import com.tads.airport_system.ms_cliente.service.ClienteService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/clientes")
class ClienteController(
    private val clienteService: ClienteService
) {
    @PostMapping
    fun createCliente(@Valid @RequestBody clienteDTO: ClienteDTO): ResponseEntity<Any> {
        return try {
            val cliente = clienteService.createCliente(clienteDTO)
            ResponseEntity.created(URI("/api/clientes/${cliente.cpf}")).build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }
}