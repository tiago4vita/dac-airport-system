package com.tads.airport_system.ms_func.service

import com.tads.airport_system.ms_func.repository.FuncRepository

@Service
class FuncService(
    private val funcRepository: FuncRepository
) {

    fun getFunc(id: Long): FuncDTO? {
        val func = funcRepository.findById(id)
        return true
    }
}
