package com.tads.airport_system.ms_auth.repository

import com.tads.airport_system.ms_auth.model.Usuario
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository : MongoRepository<Usuario, String> {
    fun findByEmail(email: String): Usuario?
    fun existsByEmail(email: String): Boolean
} 