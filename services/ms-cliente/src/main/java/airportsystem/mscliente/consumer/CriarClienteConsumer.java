package airportsystem.mscliente.consumer;

import airportsystem.mscliente.dto.ClienteDTO;
import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CriarClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public CriarClienteConsumer(ClienteRepository clienteRepository, ObjectMapper objectMapper) {
        this.clienteRepository = clienteRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "cliente.criar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        try {
            ClienteDTO clienteDTO = objectMapper.readValue(msg, ClienteDTO.class);
            Cliente novoCliente = createCliente(clienteDTO);
            System.out.println("Cliente criado via RabbitMQ: (" + novoCliente.getNome() + ") " + msg);
        } catch (ResponseStatusException e) {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public Cliente createCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsById(clienteDTO.getCpf())) {
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

        return clienteRepository.save(cliente);
    }
}
