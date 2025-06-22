package airportsystem.mscliente.consumer;

import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BuscarClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public BuscarClienteConsumer(ClienteRepository clienteRepository, ObjectMapper objectMapper) {
        this.clienteRepository = clienteRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "cliente.buscar")
    @Transactional(readOnly = true)
    public String receiveMessage(String codigo) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("Client code cannot be empty.");
            }

            Optional<Cliente> clienteOpt = clienteRepository.findById(codigo.trim());

            if (clienteOpt.isPresent()) {
                response.put("success", true);
                response.put("cliente", clienteOpt.get());
            } else {
                response.put("success", false);
                response.put("message", "Cliente with code " + codigo + " not found.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error finding client: " + e.getMessage());
        }

        return objectMapper.writeValueAsString(response);
    }
}