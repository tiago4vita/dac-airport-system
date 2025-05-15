package com.tads.airport_system.ms_func.repository

import com.tads.airport_system.ms_func.model.Func
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FuncRepository : JpaRepository<Func, Long> {
    fun findByDepartment(id: Long): Func?
    fun findByNameContainingIgnoreCase(name: String): List<Func>
}