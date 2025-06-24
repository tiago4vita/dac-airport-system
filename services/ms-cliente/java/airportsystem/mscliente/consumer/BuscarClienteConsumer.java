package airportsystem.mscliente.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import airportsystem.mscliente.config.RabbitMQConfig;
import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class BuscarClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper;

    public BuscarClienteConsumer(ClienteRepository clienteRepository,
                                 ObjectMapper objectMapper) {
        this.clienteRepository = clienteRepository;
        this.objectMapper      = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.BUSCAR_QUEUE)
    @SendTo
    public String buscar(String codigo) throws Exception {
        ObjectNode resp = objectMapper.createObjectNode();
        try {
            // Busca direto pela String
            Optional<Cliente> opt = clienteRepository.findById(codigo);
            if (opt.isEmpty()) {
                throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Cliente não encontrado"
                );
            }
            Cliente c = opt.get();

            // Monta resposta de sucesso
            resp.put("success", true);
            ObjectNode node = resp.putObject("cliente");
            node.put("codigo", c.getCodigo());
            node.put("cpf",    c.getCpf());
            node.put("nome",   c.getNome());
            node.put("email",  c.getEmail());
            // adicione demais campos conforme necessário

        } catch (ResponseStatusException ex) {
            resp.put("success",   false);
            resp.put("errorType", "NOT_FOUND");
            resp.put("message",   ex.getReason());
            resp.put("statusCode", ex.getStatusCode().value());
        } catch (Exception ex) {
            resp.put("success",   false);
            resp.put("errorType", "INTERNAL_ERROR");
            resp.put("message",   "Erro interno: " + ex.getMessage());
        }
        return objectMapper.writeValueAsString(resp);
    }
}
