package com.tads.airport_system.ms-voo.service

import com.tads.airport_system.ms_voo.model.Voo
import com.tads.airport_system.ms_voo.repository.VooRepository
import org.springframework.stereotype.Service
@Service
class VooService(
    private val vooRepository: VooRepository
) {
    fun createVoo(voo: Voo): Voo {
        return vooRepository.save(voo)
    }

    fun getAllVoos(): List<Voo> {
        return vooRepository.findAll()
    }

    fun getVooById(id: String): Voo? {
        return vooRepository.findById(id).orElse(null)
    }

    fun updateVoo(id: String, updatedVoo: Voo): Voo? {
        if (vooRepository.existsById(id)) {
            updatedVoo.id = id
            return vooRepository.save(updatedVoo)
        }
        return null
    }

    fun deleteVoo(id: String) {
        vooRepository.deleteById(id)
    }
}