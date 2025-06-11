package airportsystem.mscliente.consumer;

import airportsystem.mscliente.dto.ClienteDTO;
import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BuscarClienteConsumer(ClienteRepository clienteRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.clienteRepository = clienteRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "cliente.buscar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            // Assume the message contains the client ID to search for
            String clienteCodigo = objectMapper.readValue(msg, String.class);
            Optional<Cliente> clienteEncontrado = buscarClientePorCodigo(clienteCodigo);
            
            if (clienteEncontrado.isPresent()) {
                Cliente cliente = clienteEncontrado.get();
                System.out.println("Cliente encontrado via RabbitMQ: (" + cliente.getNome() + ") com CODIGO: " + cliente.getCodigo());
                
                // Prepare successful response
                response.put("success", true);
                response.put("cliente", cliente);
                response.put("message", "Cliente encontrado com sucesso");
            } else {
                System.err.println("Cliente com CODIGO " + clienteCodigo + " não encontrado");
                
                // Prepare error response
                response.put("success", false);
                response.put("message", "Cliente com código " + clienteCodigo + " não encontrado");
                response.put("errorType", "NOT_FOUND");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
            e.printStackTrace();
            
            // Prepare error response
            response.put("success", false);
            response.put("message", "Erro ao buscar cliente: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }
        
        // Send response to retorno queue
        try {
            String responseJson = objectMapper.writeValueAsString(response);
            rabbitTemplate.convertAndSend("retorno", responseJson);
            System.out.println("Resposta enviada para a fila retorno: " + responseJson);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
        }
    }

    /**
     * Busca um cliente pelo seu ID
     * @param codigo Codigo do cliente a ser buscado
     * @return Optional contendo o cliente, se encontrado
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorCodigo(String codigo) {
        return clienteRepository.findById(codigo);
    }
}