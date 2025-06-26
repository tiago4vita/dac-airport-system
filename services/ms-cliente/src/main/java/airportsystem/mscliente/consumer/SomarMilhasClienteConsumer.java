package airportsystem.mscliente.consumer;

import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.model.TransacaoMilhas;
import airportsystem.mscliente.repository.ClienteRepository;
import airportsystem.mscliente.repository.TransacaoMilhasRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
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
public class SomarMilhasClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final TransacaoMilhasRepository transacaoMilhasRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public SomarMilhasClienteConsumer(
            ClienteRepository clienteRepository,
            TransacaoMilhasRepository transacaoMilhasRepository,
            ObjectMapper objectMapper,
            RabbitTemplate rabbitTemplate) {
        this.clienteRepository = clienteRepository;
        this.transacaoMilhasRepository = transacaoMilhasRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "cliente.somar-milhas")
    @Transactional
    public String receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            // Parse the message as a JSON node
            JsonNode jsonNode = objectMapper.readTree(msg);
            
            // Extract values from the JSON
            String clienteCodigo = jsonNode.get("codigo").asText();
            Long clienteMilhasNovas = jsonNode.get("quantidade").asLong();
            
            Optional<Cliente> clienteEncontrado = buscarClientePorCodigo(clienteCodigo);

            if (clienteEncontrado.isPresent()) {
                Cliente cliente = clienteEncontrado.get();

                System.out.println("Cliente encontrado via RabbitMQ: (" + cliente.getNome() + ") com CODIGO: " + cliente.getCodigo());
                System.out.println("Milhas atuais: "+ cliente.getMilhas());

                // Update client miles
                cliente.setMilhas(cliente.getMilhas() + clienteMilhasNovas);
                clienteRepository.save(cliente);

                // Create transaction record
                Long valorReais = clienteMilhasNovas * 5; // Assuming 5 reais per mile
                TransacaoMilhas transacao = new TransacaoMilhas(
                    cliente,
                    clienteMilhasNovas,
                    valorReais,
                    TransacaoMilhas.TipoTransacao.ENTRADA,
                    "COMPRA DE MILHAS",
                    ""
                );
                transacaoMilhasRepository.save(transacao);

                System.out.println("Milhas após a transação: "+ cliente.getMilhas());
                System.out.println("Transação registrada com sucesso: " + transacao.getCodigo());

                // Prepare successful response
                response.put("success", true);
                response.put("codigo", cliente.getCodigo());
                response.put("saldo_milhas", cliente.getMilhas());
                response.put("message", "Milhas adicionada com sucesso!");
            } else {
                System.err.println("Cliente com CODIGO " + clienteCodigo + " não encontrado");

                // Prepare error response
                response.put("success", false);
                response.put("message", "Cliente com código " + clienteCodigo + " não encontrado");
                response.put("errorType", "NOT_FOUND");
            }
        } catch (Exception e) {
            System.err.println("Erro ao adicionar milhas para cliente: " + e.getMessage());
            e.printStackTrace();

            // Prepare error response
            response.put("success", false);
            response.put("message", "Erro ao adicionar milhas para cliente: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }

        // Return response directly
        try {
            String responseJson = objectMapper.writeValueAsString(response);
            System.out.println("Resposta retornada: " + responseJson);
            return responseJson;
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            return "{\"success\":false,\"message\":\"Erro crítico ao processar JSON\",\"errorType\":\"CRITICAL_ERROR\"}";
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