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
public class GastarMilhasClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final TransacaoMilhasRepository transacaoMilhasRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public GastarMilhasClienteConsumer(
            ClienteRepository clienteRepository,
            TransacaoMilhasRepository transacaoMilhasRepository,
            ObjectMapper objectMapper,
            RabbitTemplate rabbitTemplate) {
        this.clienteRepository = clienteRepository;
        this.transacaoMilhasRepository = transacaoMilhasRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "cliente.gastar-milhas")
    @Transactional
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            // Parse the message as a JSON node
            JsonNode jsonNode = objectMapper.readTree(msg);
            
            // Extract values from the JSON
            String clienteCodigo = jsonNode.get("codigo_cliente").asText();
            String reservaCodigo = jsonNode.get("codigo_reserva").asText();
            String aeroportoOrigem = jsonNode.get("codigo_aeroporto_origem").asText();
            String aeroportoDestino = jsonNode.get("codigo_aeroporto_destino").asText();
            Long clienteMilhasUtilizadas = jsonNode.get("milhas_utilizadas").asLong();
            
            Optional<Cliente> clienteEncontrado = buscarClientePorCodigo(clienteCodigo);

            if (!clienteEncontrado.isPresent()) {
                // Cliente não encontrado
                System.err.println("Cliente com CODIGO " + clienteCodigo + " não encontrado");
                
                // Prepare error response
                response.put("success", false);
                response.put("message", "Cliente com código " + clienteCodigo + " não encontrado");
                response.put("errorType", "NOT_FOUND");
            } 
            else if (clienteMilhasUtilizadas > clienteEncontrado.get().getMilhas()) {
                // Cliente encontrado mas com milhas insuficientes
                System.err.println("Cliente com CODIGO " + clienteCodigo + " não possui saldo suficiente de milhas");
                System.err.println("Saldo: " + clienteEncontrado.get().getMilhas() + " Milhas necessárias: " + clienteMilhasUtilizadas);
                
                // Prepare error response
                response.put("success", false);
                response.put("message", "Cliente com código " + clienteCodigo + " não possui saldo suficiente de milhas");
                response.put("errorType", "INSUFFICIENT_MILES");
            }
            else {
                // Cliente encontrado e com milhas suficientes
                Cliente cliente = clienteEncontrado.get();

                System.out.println("Cliente encontrado via RabbitMQ: (" + cliente.getNome() + ") com CODIGO: " + cliente.getCodigo());
                System.out.println("Milhas atuais: "+ cliente.getMilhas());

                // Update client miles
                cliente.setMilhas(cliente.getMilhas() - clienteMilhasUtilizadas);
                clienteRepository.save(cliente);

                // Create transaction record
                Long valorReais = clienteMilhasUtilizadas * 5; // Assuming 5 reais per mile
                TransacaoMilhas transacao = new TransacaoMilhas(
                    cliente,
                    clienteMilhasUtilizadas,
                    valorReais,
                    TransacaoMilhas.TipoTransacao.SAIDA,
                    aeroportoOrigem + "->" + aeroportoDestino,
                    reservaCodigo
                );
                transacaoMilhasRepository.save(transacao);

                System.out.println("Milhas após a transação: "+ cliente.getMilhas());
                System.out.println("Transação registrada com sucesso: " + transacao.getCodigo());

                // Prepare successful response
                response.put("success", true);
                response.put("codigo", cliente.getCodigo());
                response.put("saldo_milhas", cliente.getMilhas());
                response.put("message", "Milhas adicionada com sucesso!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao realizar transação milhas: " + e.getMessage());
            e.printStackTrace();

            // Prepare error response
            response.put("success", false);
            response.put("message", "Erro ao realizar transação milhas: " + e.getMessage());
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