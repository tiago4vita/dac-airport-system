package com.tads.airport_system.ms_func.service

import com.tads.airport_system.ms_func.repository.FuncRepository
import com.tads.airport_system.ms_func.dto.FuncDTO

@Service
class FuncService(
    private val funcRepository: FuncRepository
) {

    fun getFunc(id: Long): FuncDTO? {
        return funcRepository.findById(id)?.let { func ->
            FuncDTO.fromModel(func)
        }
    }
}
