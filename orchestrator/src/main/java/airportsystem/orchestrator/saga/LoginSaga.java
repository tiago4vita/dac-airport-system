package airportsystem.orchestrator.saga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import airportsystem.orchestrator.dto.LoginRequestDTO;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginSaga {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public LoginSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute(LoginRequestDTO loginRequest) {
        try {
            // Step 1: Authenticate with ms-auth
            String authResponseJson = (String) rabbitTemplate.convertSendAndReceive("auth.login", objectMapper.writeValueAsString(loginRequest));
            if (authResponseJson == null) {
                return createErrorResponse("Authentication service timeout");
            }

            JsonNode authResponse = objectMapper.readTree(authResponseJson);
            if (!authResponse.get("success").asBoolean()) {
                return authResponseJson; // Return auth error response directly
            }

            // Check if required fields exist
            if (!authResponse.has("codigo") || !authResponse.has("tipo")) {
                return createErrorResponse("Authentication response missing required fields (codigo or tipo)");
            }

            String codigo = authResponse.get("codigo").asText();
            String tipo = authResponse.get("tipo").asText();

            // Step 2: Fetch user data based on 'tipo'
            String userDataJson;
            if ("CLIENTE".equals(tipo)) {
                userDataJson = (String) rabbitTemplate.convertSendAndReceive("cliente.buscar", codigo);
            } else if ("FUNCIONARIO".equals(tipo)) {
                userDataJson = (String) rabbitTemplate.convertSendAndReceive("funcionario.buscar", codigo);
            } else {
                return createErrorResponse("Unknown user type: " + tipo);
            }

            if (userDataJson == null) {
                return createErrorResponse("User data service timeout for type: " + tipo);
            }

            JsonNode userDataResponse = objectMapper.readTree(userDataJson);
            if (!userDataResponse.get("success").asBoolean()) {
                return userDataJson; // Return user data error response
            }

            // Step 3: Combine and format the final response
            return createSuccessResponse(tipo, userDataResponse, authResponse);

        } catch (Exception e) {
            return createErrorResponse("Internal error during login saga: " + e.getMessage());
        }
    }

    private String createSuccessResponse(String tipo, JsonNode userDataResponse, JsonNode authResponse) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectNode finalResponse = objectMapper.createObjectNode();
        finalResponse.put("success", true);
        finalResponse.put("token_type", "bearer"); // Placeholder, will be populated by API Gateway
        finalResponse.put("tipo", tipo);
        
        String userField = "CLIENTE".equals(tipo) ? "cliente" : "funcionario";
        if (userDataResponse.has(userField)) {
            finalResponse.set("usuario", userDataResponse.get(userField));
        }
        
        // Add email from auth response
        if (authResponse.has("email")) {
            finalResponse.put("email", authResponse.get("email").asText());
        }

        return objectMapper.writeValueAsString(finalResponse);
    }

    private String createErrorResponse(String message) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("success", false);
        errorMap.put("message", message);
        errorMap.put("errorType", "SAGA_ERROR");
        try {
            return objectMapper.writeValueAsString(errorMap);
        } catch (Exception e) {
            return "{\"success\":false, \"message\":\"Failed to serialize error response\"}";
        }
    }
} 