package airportsystem.orchestrator.saga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BuscarClienteSaga {

    private static final Logger logger = LoggerFactory.getLogger(BuscarClienteSaga.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public BuscarClienteSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute(String codigoCliente) {
        try {
            logger.info("Starting BuscarClienteSaga for codigo: {}", codigoCliente);
            
            // Validate input
            if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
                return createErrorResponse("Client code cannot be empty", "VALIDATION_ERROR");
            }

            // Send request to ms-cliente
            String response = sendWithRetry("cliente.buscar", codigoCliente.trim());
            
            if (response == null) {
                return createErrorResponse("Cliente search request timed out", "TIMEOUT_ERROR");
            }

            // Parse response
            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") && responseJson.get("success").asBoolean();
            
            if (!success) {
                String message = responseJson.has("message") ? 
                    responseJson.get("message").asText() : "Cliente not found";
                return createErrorResponse(message, "NOT_FOUND_ERROR");
            }

            // Extract cliente data and format response
            if (responseJson.has("cliente")) {
                JsonNode clienteNode = responseJson.get("cliente");
                return formatClienteResponse(clienteNode);
            } else {
                return createErrorResponse("Cliente data not found in response", "INTERNAL_ERROR");
            }

        } catch (Exception e) {
            logger.error("Error in BuscarClienteSaga: {}", e.getMessage(), e);
            return createErrorResponse("Error processing cliente search: " + e.getMessage(), "INTERNAL_ERROR");
        }
    }

    private String formatClienteResponse(JsonNode clienteNode) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectNode formattedResponse = objectMapper.createObjectNode();
        
        // Map the fields to the expected format
        if (clienteNode.has("codigo")) {
            formattedResponse.put("codigo", clienteNode.get("codigo").asText());
        }
        if (clienteNode.has("cpf")) {
            formattedResponse.put("cpf", clienteNode.get("cpf").asText());
        }
        if (clienteNode.has("email")) {
            formattedResponse.put("email", clienteNode.get("email").asText());
        }
        if (clienteNode.has("nome")) {
            formattedResponse.put("nome", clienteNode.get("nome").asText());
        }
        if (clienteNode.has("milhas")) {
            formattedResponse.put("saldo_milhas", clienteNode.get("milhas").asLong());
        } else {
            formattedResponse.put("saldo_milhas", 0);
        }
        
        // Format endereco
        ObjectNode enderecoNode = objectMapper.createObjectNode();
        if (clienteNode.has("cep")) {
            enderecoNode.put("cep", clienteNode.get("cep").asText());
        }
        if (clienteNode.has("uf")) {
            enderecoNode.put("uf", clienteNode.get("uf").asText());
        }
        if (clienteNode.has("cidade")) {
            enderecoNode.put("cidade", clienteNode.get("cidade").asText());
        }
        if (clienteNode.has("bairro")) {
            enderecoNode.put("bairro", clienteNode.get("bairro").asText());
        }
        if (clienteNode.has("rua")) {
            enderecoNode.put("rua", clienteNode.get("rua").asText());
        }
        if (clienteNode.has("numero")) {
            enderecoNode.put("numero", clienteNode.get("numero").asText());
        }
        if (clienteNode.has("complemento")) {
            enderecoNode.put("complemento", clienteNode.get("complemento").asText());
        }
        
        formattedResponse.set("endereco", enderecoNode);
        
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