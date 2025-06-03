package com.example.airport_system.ms_voo.service

import com.example.airport_system.ms_voo.dto.AeroportoDTO
import com.example.airport_system.ms_voo.dto.VooDTO
import com.example.airport_system.ms_voo.model.Voo
import com.example.airport_system.ms_voo.repository.VooRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

@Service
class VooService(
    private val vooRepository: VooRepository
    // Considere injetar AeroportoRepository se precisar buscar/validar AeroportoDTOs
    // private val aeroportoRepository: AeroportoRepository
) {

    fun createVoo(vooDTO: VooDTO): VooDTO {
        val vooEntidade = Voo(
            origem = vooDTO.aeroportoOrigem.codigo,
            destino = vooDTO.aeroportoDestino.codigo, 
            dataHora = vooDTO.data,
            preco = vooDTO.valorPassagem.toDouble(),
            qtdPoltronas = vooDTO.quantidadePoltTotal,
            status = vooDTO.estado
        )

        val savedVooEntidade = vooRepository.save(vooEntidade)

        return VooDTO(
            codigo = savedVooEntidade.id,
            origem = savedVooEntidade.origem,
            destino = savedVooEntidade.destino, 
            data = savedVooEntidade.dataHora,
            estado = savedVooEntidade.status,
            valorPassagem = BigDecimal.valueOf(savedVooEntidade.preco), 
            quantidadePoltTotal = savedVooEntidade.qtdPoltronas,
            quantidadePoltOcup = vooDTO.quantidadePoltOcup, 
            aeroportoOrigem = AeroportoDTO(codigo = savedVooEntidade.origem, nome = "", cidade = "", estado = ""), 
            aeroportoDestino = AeroportoDTO(codigo = savedVooEntidade.destino, nome = "", cidade = "", estado = "") 
        )
    }

    fun getAllVoos(): List<Voo> {
        return vooRepository.findAll()
    }

    fun getVooById(id: String): Voo? {
        val vooOptional: Optional<Voo> = vooRepository.findById(id)
        return vooOptional.orElse(null)
    }

    fun updateVoo(id: String, vooComNovosDados: Voo): Voo? {
        val vooExistenteOptional: Optional<Voo> = vooRepository.findById(id)
        if (vooExistenteOptional.isPresent) {
            val vooExistente = vooExistenteOptional.get()
            vooExistente.origem = vooComNovosDados.origem
            vooExistente.destino = vooComNovosDados.destino
            vooExistente.dataHora = vooComNovosDados.dataHora
            vooExistente.preco = vooComNovosDados.preco
            vooExistente.qtdPoltronas = vooComNovosDados.qtdPoltronas
            vooExistente.status = vooComNovosDados.status
            return vooRepository.save(vooExistente)
        }
        return null
    }

    fun deleteVooById(id: String) {
        if (vooRepository.existsById(id)) {
            vooRepository.deleteById(id)
        }
    }
}