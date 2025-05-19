package com.tads.airport_system.ms_voo.repository.VooRepository

import com.tads.airport_system.ms_voo.model.Voo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VooRepository : JpaRepository<Voo, String> {
    // Aqui você pode adicionar métodos personalizados, se necessário
    fun findByOrigem(origem: String): List<Voo>
    fun findByDestino(destino: String): List<Voo>
    fun findByDataHora(dataHora: String): List<Voo>
}
