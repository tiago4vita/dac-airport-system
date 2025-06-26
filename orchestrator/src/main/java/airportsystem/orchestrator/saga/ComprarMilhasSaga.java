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

            if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
                return createErrorResponse("Client code cannot be empty", "VALIDATION_ERROR");
            }
            if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
                return createErrorResponse("Quantidade must be a positive number", "VALIDATION_ERROR");
            }

            // Monta payload JSON
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("codigo", codigoCliente.trim());
            payload.put("quantidade", request.getQuantidade());
            String payloadJson = objectMapper.writeValueAsString(payload);

            // Envia com retry
            String response = sendWithRetry("cliente.somar-milhas", payloadJson);
            if (response == null) {
                return createErrorResponse("Miles purchase request timed out", "TIMEOUT_ERROR");
            }

            JsonNode respJson = objectMapper.readTree(response);
            if (!respJson.path("success").asBoolean(false)) {
                String msg = respJson.path("message").asText("Failed to purchase miles");
                return createErrorResponse(msg, "INTERNAL_ERROR");
            }

            // Formata só os campos esperados pelo front
            ObjectNode formatted = objectMapper.createObjectNode();
            formatted.put("codigo", respJson.path("codigo").asText());
            formatted.put("saldo_milhas", respJson.path("saldo_milhas").asLong());
            return objectMapper.writeValueAsString(formatted);

        } catch (Exception e) {
            logger.error("Error in ComprarMilhasSaga: {}", e.getMessage(), e);
            return createErrorResponse("Error processing miles purchase: " + e.getMessage(), "INTERNAL_ERROR");
        }
    }

    private String sendWithRetry(String queueName, String message) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.debug("Attempt {}/{} sending to queue {}", attempt, MAX_RETRIES, queueName);
                String resp = (String) rabbitTemplate.convertSendAndReceive(queueName, message);
                if (resp != null) {
                    return resp;
                }
            } catch (Exception e) {
                logger.warn("Attempt {} failed for {}: {}", attempt, queueName, e.getMessage());
            }
            try {
                Thread.sleep(RETRY_DELAY_MS * attempt);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        logger.error("All {} attempts failed for queue {}", MAX_RETRIES, queueName);
        return null;
    }

    private String createErrorResponse(String message, String errorType) {
        try {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("success", false);
            error.put("message", message);
            error.put("errorType", errorType);
            return objectMapper.writeValueAsString(error);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Failed to serialize error response\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
}
