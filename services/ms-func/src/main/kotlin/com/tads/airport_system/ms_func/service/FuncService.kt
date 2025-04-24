package com.tads.airport_system.ms_func.service

import com.tads.airport_system.ms_func.repository.FuncRepository
import com.tads.airport_system.ms_func.dto.FuncDTO
import org.springframework.stereotype.Service
import com.tads.airport_system.ms_func.model.Func

@Service
class FuncService(
    private val funcRepository: FuncRepository
) {

    fun getFunc(id: Long): FuncDTO? {
        return funcRepository.findById(id).orElse(null)?.let { func ->
            FuncDTO.fromModel(func)
        }
    }

    fun createFunc(funcDTO: FuncDTO): FuncDTO {
        val func = funcDTO.toModel() 
        val savedFunc = funcRepository.save(func) 
        return FuncDTO.fromModel(savedFunc)
    }

    fun getAllFuncs(): List<FuncDTO> {
        return funcRepository.findAll().map { func ->
            FuncDTO.fromModel(func)
        }
    }

    fun updateFunc(id: Long, funcDTO: FuncDTO): FuncDTO? {
        val existingFunc = funcRepository.findById(id).orElse(null)
        return if (existingFunc != null) {
            val updatedFunc = funcDTO.toModel().apply { this.id = id }
            val savedFunc = funcRepository.save(updatedFunc)
            FuncDTO.fromModel(savedFunc)
        } else {
            null
        }
    }

    // Delete
    fun deleteFunc(id: Long): Boolean {
        return if (funcRepository.existsById(id)) {
            funcRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun findByName(name: String): List<FuncDTO> {
        return funcRepository.?findByNameContainingIgnoreCase(name).map { func ->
            FuncDTO.fromModel(func)
        }
    }

    // Count Total Employees
    fun countFuncs(): Long {
        return funcRepository.count()
    }
}
