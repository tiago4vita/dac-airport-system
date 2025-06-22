package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.dto.LoginRequestDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@CrossOrigin
public class AuthController {
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    public AuthController(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Create a Map with the required format
            Map<String, String> loginPayload = new HashMap<>();
            loginPayload.put("login", loginRequest.getLogin());
            loginPayload.put("senha", loginRequest.getSenha());

            // Convert Map to JSON string
            String jsonPayload = objectMapper.writeValueAsString(loginPayload);

            // Send the JSON string to RabbitMQ and wait for response
            String response = (String) rabbitTemplate.convertSendAndReceive("auth.login", jsonPayload);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Login request timed out");
            }

            // Parse the response
            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") && responseJson.get("success").asBoolean();

            if (success) {
                System.out.println(responseJson);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseJson.toString());
            } else {
                String message = responseJson.has("message") ?
                        responseJson.get("message").asText() : "Authentication failed";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseJson.toString());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error processing login request: " + e.getMessage());
        }
    }
}