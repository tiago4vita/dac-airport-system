package com.tads.airport_system.ms_cliente.repository

import com.tads.airport_system.ms_cliente.model.TransacaoMilhas
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransacaoMilhasRepository : JpaRepository<TransacaoMilhas, Long> {
    fun findByClienteCpfOrderByDataHoraDesc(cpf: String): List<TransacaoMilhas>
} 