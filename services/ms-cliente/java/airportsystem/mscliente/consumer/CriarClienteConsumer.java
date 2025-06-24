package airportsystem.mscliente.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import airportsystem.mscliente.config.RabbitMQConfig;
import airportsystem.mscliente.dto.ClienteDTO;
import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CriarClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper;

    public CriarClienteConsumer(ClienteRepository clienteRepository,
                                ObjectMapper objectMapper) {
        this.clienteRepository = clienteRepository;
        this.objectMapper      = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.CRIAR_QUEUE)
    public String criar(String msg) throws Exception {
        ObjectNode response = objectMapper.createObjectNode();
        try {
            // Desserializa
            ClienteDTO dto = objectMapper.readValue(msg, ClienteDTO.class);

            // Valida e salva
            Cliente c = salvaCliente(dto);

            // Monta resposta de sucesso
            ObjectNode clienteNode = response.putObject("cliente");
            clienteNode.put("codigo", c.getCodigo().toString());
            clienteNode.put("cpf",    c.getCpf());
            clienteNode.put("nome",   c.getNome());
            clienteNode.put("email",  c.getEmail());
            response.put("success", true);
            response.put("message", "Cliente criado com sucesso");

        } catch (ResponseStatusException ex) {
            // Validação
            response.put("success",    false);
            response.put("errorType",  "VALIDATION_ERROR");
            response.put("message",    ex.getReason());
            response.put("statusCode", ex.getStatusCode().value());
        } catch (Exception ex) {
            // Erro inesperado
            response.put("success",   false);
            response.put("errorType", "INTERNAL_ERROR");
            response.put("message",   "Erro interno: " + ex.getMessage());
        }
        // Retorna JSON que o RabbitTemplate enviará ao reply-to
        return objectMapper.writeValueAsString(response);
    }

    @Transactional
    private Cliente salvaCliente(ClienteDTO dto) {
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cliente com CPF " + dto.getCpf() + " já existe"
            );
        }
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cliente com email " + dto.getEmail() + " já existe"
            );
        }
        Cliente c = new Cliente(dto.getCpf(), dto.getNome(), dto.getEmail(), dto.getEndereco());
        return clienteRepository.save(c);
    }
}
