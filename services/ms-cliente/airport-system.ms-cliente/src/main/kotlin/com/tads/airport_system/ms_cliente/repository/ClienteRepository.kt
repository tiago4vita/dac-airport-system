package com.tads.airport_system.ms_cliente.repository

import com.tads.airport_system.ms_cliente.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository : JpaRepository<Cliente, String> {
    fun findByEmail(email: String): Cliente?
    fun existsByEmail(email: String): Boolean
} 