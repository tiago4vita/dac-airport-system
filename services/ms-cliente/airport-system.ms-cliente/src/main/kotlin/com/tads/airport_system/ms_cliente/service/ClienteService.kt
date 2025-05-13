package com.tads.airport_system.ms_cliente.service

import com.tads.airport_system.ms_cliente.dto.ClienteDTO
import com.tads.airport_system.ms_cliente.model.Cliente
import com.tads.airport_system.ms_cliente.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(
    private val clienteRepository: ClienteRepository
) {
    @Transactional
    fun createCliente(clienteDTO: ClienteDTO): Cliente {
        if (clienteRepository.existsById(clienteDTO.cpf)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Cliente com CPF ${clienteDTO.cpf} já existe")
        }

        if (clienteRepository.existsByEmail(clienteDTO.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Cliente com email ${clienteDTO.email} já existe")
        }

        val cliente = Cliente(
            cpf = clienteDTO.cpf,
            nome = clienteDTO.nome,
            email = clienteDTO.email,
            rua = clienteDTO.endereco.rua,
            numero = clienteDTO.endereco.numero,
            complemento = clienteDTO.endereco.complemento,
            bairro = clienteDTO.endereco.bairro,
            cep = clienteDTO.endereco.cep,
            cidade = clienteDTO.endereco.cidade,
            uf = clienteDTO.endereco.uf,
            milhas = clienteDTO.saldo_milhas
        )

        return clienteRepository.save(cliente)
    }
} 