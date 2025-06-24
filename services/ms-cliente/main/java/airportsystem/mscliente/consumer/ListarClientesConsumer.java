// services/ms-cliente/java/airportsystem/mscliente/consumer/ListarClientesConsumer.java
package airportsystem.mscliente.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import airportsystem.mscliente.config.RabbitMQConfig;
import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarClientesConsumer {

    private final ClienteRepository repo;
    private final ObjectMapper mapper;

    public ListarClientesConsumer(ClienteRepository repo, ObjectMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }

    @RabbitListener(queues = RabbitMQConfig.LISTAR_QUEUE)
    public String listarTodos() throws Exception {
        List<Cliente> clientes = repo.findAll();

        // Monta um ArrayNode com cada cliente como um ObjectNode
        ArrayNode arr = mapper.createArrayNode();
        for (Cliente c : clientes) {
            ObjectNode obj = mapper.createObjectNode();
            obj.put("codigo",       c.getCodigo());
            obj.put("cpf",          c.getCpf());
            obj.put("nome",         c.getNome());
            obj.put("email",        c.getEmail());
            obj.put("saldo_milhas", c.getMilhas());
            // adicione outros campos que quiser expor...
            arr.add(obj);
        }

        // Retorna o JSON do array; o RabbitTemplate responde ao reply-to automaticamente
        return mapper.writeValueAsString(arr);
    }
}
