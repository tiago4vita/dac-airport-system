package com.tads.airport_system.ms_func.repository

import com.tads.airport_system.ms_func.model.Func
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface FuncRepository : JpaRepository<Func, Long> {
    fun findById(id: Long): Func?
}