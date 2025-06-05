package airportsystem.mscliente.service;

import airportsystem.mscliente.dto.ClienteDTO;
import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente createCliente(ClienteDTO clienteDTO) {

        if (clienteRepository.existsByCpf(clienteDTO.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cliente com CPF " + clienteDTO.getCpf() + " já existe");
        }

        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cliente com email " + clienteDTO.getEmail() + " já existe");
        }

        Cliente cliente = new Cliente(
                clienteDTO.getCpf(),
                clienteDTO.getNome(),
                clienteDTO.getEmail(),
                clienteDTO.getEndereco()
        );

        // Set milhas from DTO
        cliente.setMilhas(clienteDTO.getSaldoMilhas());

        return clienteRepository.save(cliente);
    }
}