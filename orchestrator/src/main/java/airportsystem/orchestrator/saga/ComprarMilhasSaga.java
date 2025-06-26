package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.ComprarMilhasRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ComprarMilhasSaga {

    private static final Logger logger = LoggerFactory.getLogger(ComprarMilhasSaga.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ComprarMilhasSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute(String codigoCliente, ComprarMilhasRequestDTO request) {
        try {
            logger.info("Starting ComprarMilhasSaga for cliente: {} with quantidade: {}", codigoCliente, request.getQuantidade());
            
            // Validate input
            if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
                return createErrorResponse("Client code cannot be empty", "VALIDATION_ERROR");
            }
            
            if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
                return createErrorResponse("Quantidade must be a positive number", "VALIDATION_ERROR");
            }

            // Create payload for ms-cliente
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("codigo", codigoCliente.trim());
            payload.put("quantidade", request.getQuantidade());
            
            String payloadJson = objectMapper.writeValueAsString(payload);
            logger.debug("Sending payload to cliente.somar-milhas: {}", payloadJson);

            // Send request to ms-cliente
            String response = sendWithRetry("cliente.somar-milhas", payloadJson);
            
            if (response == null) {
                return createErrorResponse("Miles purchase request timed out", "TIMEOUT_ERROR");
            }

            // Parse response
            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") && responseJson.get("success").asBoolean();
            
            if (!success) {
                String message = responseJson.has("message") ? 
                    responseJson.get("message").asText() : "Failed to purchase miles";
                return createErrorResponse(message, "INTERNAL_ERROR");
            }

            // Format response according to test requirements
            return formatResponse(responseJson);

        } catch (Exception e) {
            logger.error("Error in ComprarMilhasSaga: {}", e.getMessage(), e);
            return createErrorResponse("Error processing miles purchase: " + e.getMessage(), "INTERNAL_ERROR");
        }
    }

    private String formatResponse(JsonNode responseJson) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectNode formattedResponse = objectMapper.createObjectNode();
        
        // Map the fields to the expected format
        if (responseJson.has("codigo")) {
            formattedResponse.put("codigo", responseJson.get("codigo").asText());
        }
        if (responseJson.has("saldo_milhas")) {
            formattedResponse.put("saldo_milhas", responseJson.get("saldo_milhas").asLong());
        }
        
        return objectMapper.writeValueAsString(formattedResponse);
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