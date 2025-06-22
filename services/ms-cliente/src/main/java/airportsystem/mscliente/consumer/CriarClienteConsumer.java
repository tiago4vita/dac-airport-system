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

import java.util.HashMap;
import java.util.Map;

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
    public String receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mensagem vazia. A mensagem deve conter os dados do cliente.");
                response.put("errorType", "VALIDATION_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            System.out.println("Criar cliente recebido via RabbitMQ: " + msg);
            
            ClienteDTO clienteDTO = objectMapper.readValue(msg, ClienteDTO.class);
            Cliente novoCliente = createCliente(clienteDTO);
            System.out.println("Cliente criado via RabbitMQ: (" + novoCliente.getNome() + ") " + msg);
            
            // Prepare successful response
            response.put("success", true);
            
            Map<String, Object> clienteData = new HashMap<>();
            clienteData.put("codigo", novoCliente.getCodigo());
            clienteData.put("cpf", novoCliente.getCpf());
            clienteData.put("nome", novoCliente.getNome());
            clienteData.put("email", novoCliente.getEmail());
            clienteData.put("milhas", novoCliente.getMilhas());
            clienteData.put("dataCriacao", novoCliente.getDataCriacao());
            
            response.put("cliente", clienteData);
            response.put("message", "Cliente criado com sucesso");
            
        } catch (ResponseStatusException e) {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
            
            // Prepare error response
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errorType", "VALIDATION_ERROR");
            response.put("statusCode", e.getStatusCode().value());
            
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
            
            // Prepare error response
            response.put("success", false);
            response.put("message", "Erro interno: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }
        
        // Return response directly
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            return "{\"success\":false,\"message\":\"Erro crítico ao processar JSON\",\"errorType\":\"CRITICAL_ERROR\"}";
        }
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

        return clienteRepository.save(cliente);
    }
}