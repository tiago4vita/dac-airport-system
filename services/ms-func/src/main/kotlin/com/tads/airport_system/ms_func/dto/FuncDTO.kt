package com.tads.airport_system.ms_func.dto
import com.tads.airport_system.ms_func.model.Func

data class FuncDTO(
    val id: Long,
    val userId: Long, // ID do Usuário no microsserviço auth
    val role: String, // Cargo do funcionário
    val department: String // Departamento do funcionário
) {
    companion object {
        fun fromModel(func: Func): FuncDTO {
            return FuncDTO(
                id = func.id,
                userId = func.userId,
                role = func.role,
                department = func.department
            )
        }
    }

    fun toModel(): Func {
        return Func(
            id = id,
            userId = userId,
            role = role,
            department = department
        )
    }
}