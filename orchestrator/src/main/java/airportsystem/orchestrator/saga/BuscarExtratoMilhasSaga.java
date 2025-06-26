package airportsystem.orchestrator.saga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BuscarExtratoMilhasSaga {

    private static final Logger logger = LoggerFactory.getLogger(BuscarExtratoMilhasSaga.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public BuscarExtratoMilhasSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute(String codigoCliente) {
        try {
            logger.info("Starting BuscarExtratoMilhasSaga for cliente: {}", codigoCliente);
            
            // Validate input
            if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
                return createErrorResponse("Client code cannot be empty", "VALIDATION_ERROR");
            }

            // Send request to ms-cliente
            String response = sendWithRetry("cliente.buscar-extrato-milhas", codigoCliente.trim());
            
            if (response == null) {
                return createErrorResponse("Miles statement request timed out", "TIMEOUT_ERROR");
            }

            // Parse response
            JsonNode responseJson = objectMapper.readTree(response);
            
            // Check if it's an error response
            if (responseJson.has("success") && !responseJson.get("success").asBoolean()) {
                String message = responseJson.has("message") ? 
                    responseJson.get("message").asText() : "Failed to get miles statement";
                return createErrorResponse(message, "INTERNAL_ERROR");
            }

            // Return the response directly as it's already in the correct format
            return response;

        } catch (Exception e) {
            logger.error("Error in BuscarExtratoMilhasSaga: {}", e.getMessage(), e);
            return createErrorResponse("Error processing miles statement request: " + e.getMessage(), "INTERNAL_ERROR");
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

    private String createErrorResponse(String message, String errorType) {
        try {
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("success", false);
            errorResponse.put("message", message);
            errorResponse.put("errorType", errorType);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Failed to serialize error response\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
} 