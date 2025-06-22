package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.ClienteRequestDTO;
import airportsystem.orchestrator.dto.UsuarioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CriarClienteSaga {

    private static final Logger logger = LoggerFactory.getLogger(CriarClienteSaga.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CriarClienteSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
    
    public String execute(ClienteRequestDTO clienteRequest) {
        String clienteCodigo = null;
        
        try {
            logger.info("Starting CriarClienteSaga for email: {}", clienteRequest.getEmail());
            
            // Step 1: Create cliente (without senha)
            ClienteRequestDTO clienteWithoutSenha = new ClienteRequestDTO(
                clienteRequest.getCpf(),
                clienteRequest.getEmail(),
                clienteRequest.getNome(),
                clienteRequest.getSaldoMilhas(),
                clienteRequest.getEndereco(),
                null // Don't send senha to ms-cliente
            );
            
            String clienteJsonPayload = objectMapper.writeValueAsString(clienteWithoutSenha);
            String clienteResponse = sendWithRetry("cliente.criar", clienteJsonPayload);
            
            if (clienteResponse == null) {
                return "{\"success\":false,\"message\":\"Cliente creation request timed out\",\"errorType\":\"TIMEOUT_ERROR\"}";
            }
            
            // Parse cliente response
            JsonNode clienteResponseJson = objectMapper.readTree(clienteResponse);
            boolean clienteSuccess = clienteResponseJson.has("success") && clienteResponseJson.get("success").asBoolean();
            
            if (!clienteSuccess) {
                logger.warn("Cliente creation failed: {}", clienteResponse);
                return clienteResponse; // Return the error from ms-cliente
            }
            
            // Extract cliente codigo for compensation if needed
            if (clienteResponseJson.has("cliente") && clienteResponseJson.get("cliente").has("codigo")) {
                clienteCodigo = clienteResponseJson.get("cliente").get("codigo").asText();
                logger.info("Cliente created successfully with codigo: {}", clienteCodigo);
            }
            
            // Step 2: Create usuario
            UsuarioRequestDTO usuarioRequest = new UsuarioRequestDTO(
                clienteCodigo,
                clienteRequest.getEmail(), // Use email as login
                clienteRequest.getSenha(),
                "CLIENTE"
            );
            
            String usuarioJsonPayload = objectMapper.writeValueAsString(usuarioRequest);
            String usuarioResponse = sendWithRetry("auth.criar", usuarioJsonPayload);
            
            if (usuarioResponse == null) {
                // Compensate: Delete cliente
                if (clienteCodigo != null) {
                    logger.warn("Usuario creation timed out, compensating by deleting cliente: {}", clienteCodigo);
                    rabbitTemplate.convertAndSend("cliente.deletar", clienteCodigo);
                }
                return "{\"success\":false,\"message\":\"Usuario creation request timed out\",\"errorType\":\"TIMEOUT_ERROR\"}";
            }
            
            // Parse usuario response
            JsonNode usuarioResponseJson = objectMapper.readTree(usuarioResponse);
            boolean usuarioSuccess = usuarioResponseJson.has("success") && usuarioResponseJson.get("success").asBoolean();
            
            if (!usuarioSuccess) {
                // Compensate: Delete cliente
                if (clienteCodigo != null) {
                    logger.warn("Usuario creation failed, compensating by deleting cliente: {}", clienteCodigo);
                    rabbitTemplate.convertAndSend("cliente.deletar", clienteCodigo);
                }
                logger.warn("Usuario creation failed: {}", usuarioResponse);
                return usuarioResponse; // Return the error from ms-auth
            }
            
            logger.info("Both cliente and usuario created successfully");
            
            // Both operations successful - return cliente data in the specified format
            if (clienteResponseJson.has("cliente")) {
                return clienteResponseJson.get("cliente").toString();
            } else {
                // Fallback if cliente data is not in expected format
                return "{\"success\":true,\"message\":\"Cliente and usuario created successfully\",\"cliente\":" + 
                       clienteResponseJson.get("cliente").toString() + 
                       ",\"usuario\":" + usuarioResponseJson.get("usuario").toString() + "}";
            }
            
        } catch (Exception e) {
            logger.error("Error in CriarClienteSaga: {}", e.getMessage(), e);
            
            // Compensate: Delete cliente if it was created
            if (clienteCodigo != null) {
                try {
                    logger.warn("Compensating by deleting cliente: {}", clienteCodigo);
                    rabbitTemplate.convertAndSend("cliente.deletar", clienteCodigo);
                } catch (Exception compensationException) {
                    // Log compensation failure but don't throw
                    logger.error("Failed to compensate cliente deletion: {}", compensationException.getMessage(), compensationException);
                }
            }
            
            return "{\"success\":false,\"message\":\"Error processing cliente creation: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
    
    private String sendWithRetry(String queueName, String message) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.debug("Sending message to queue {} (attempt {}/{})", queueName, attempt, MAX_RETRIES);
                String response = (String) rabbitTemplate.convertSendAndReceive(queueName, message);
                if (response != null) {
                    logger.debug("Successfully received response from queue {}", queueName);
                    return response;
                }
            } catch (Exception e) {
                logger.warn("Attempt {} failed for queue {}: {}", attempt, queueName, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        logger.error("All {} attempts failed for queue {}", MAX_RETRIES, queueName);
        return null;
    }
} 