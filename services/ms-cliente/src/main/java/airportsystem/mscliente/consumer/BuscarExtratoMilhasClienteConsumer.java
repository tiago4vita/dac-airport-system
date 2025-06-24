package airportsystem.mscliente.consumer;

import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.model.TransacaoMilhas;
import airportsystem.mscliente.repository.ClienteRepository;
import airportsystem.mscliente.repository.TransacaoMilhasRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BuscarExtratoMilhasClienteConsumer {

    private final ClienteRepository clienteRepository;
    private final TransacaoMilhasRepository transacaoMilhasRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BuscarExtratoMilhasClienteConsumer(
            ClienteRepository clienteRepository, 
            TransacaoMilhasRepository transacaoMilhasRepository,
            ObjectMapper objectMapper, 
            RabbitTemplate rabbitTemplate) {
        this.clienteRepository = clienteRepository;
        this.transacaoMilhasRepository = transacaoMilhasRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "cliente.buscar-extrato-milhas")
    public String receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {

            String clienteCodigo = msg.trim();
            Optional<Cliente> clienteEncontrado = buscarClientePorCodigo(clienteCodigo);
            
            if (clienteEncontrado.isPresent()) {
                Cliente cliente = clienteEncontrado.get();
                System.out.println("Cliente encontrado via RabbitMQ: (" + cliente.getNome() + ") com CODIGO: " + cliente.getCodigo());
                
                // Get client's transactions
                List<TransacaoMilhas> transacoes = transacaoMilhasRepository.findByClienteCodigoOrderByDataHoraAsc(clienteCodigo);
                
                // Prepare the transaction list for the response
                List<Map<String, Object>> transacoesResponse = new ArrayList<>();
                for (TransacaoMilhas transacao : transacoes) {
                    Map<String, Object> transacaoMap = new HashMap<>();
                    // Format the date in the required format (with timezone)
                    ZonedDateTime zonedDateTime = transacao.getDataHora().atZone(ZoneId.of("America/Sao_Paulo"));
                    String formattedDateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
                    
                    transacaoMap.put("data", formattedDateTime);
                    // Convert Long to Double for decimal values
                    transacaoMap.put("valor_reais", transacao.getValorReais() / 100.0);
                    transacaoMap.put("quantidade_milhas", transacao.getQuantidade());
                    transacaoMap.put("descricao", transacao.getDescricao());
                    transacaoMap.put("codigo_reserva", transacao.getCodigoReserva() != null ? transacao.getCodigoReserva() : "");
                    transacaoMap.put("tipo", transacao.getTipo().toString());
                    
                    transacoesResponse.add(transacaoMap);
                }
                
                // Prepare successful response
                response.put("codigo", cliente.getCodigo());
                response.put("saldo_milhas", cliente.getMilhas());
                response.put("transacoes", transacoesResponse);
            } else {
                System.err.println("Cliente com CODIGO " + clienteCodigo + " não encontrado");
                
                // Prepare error response
                response.put("success", false);
                response.put("message", "Cliente com código " + clienteCodigo + " não encontrado");
                response.put("errorType", "NOT_FOUND");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar extrato de milhas do cliente: " + e.getMessage());
            e.printStackTrace();
            
            // Prepare error response
            response.put("success", false);
            response.put("message", "Erro ao buscar extrato de milhas do cliente: " + e.getMessage());
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