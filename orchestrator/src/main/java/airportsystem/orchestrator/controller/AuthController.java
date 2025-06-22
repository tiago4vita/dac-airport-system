package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.dto.LoginRequestDTO;
import airportsystem.orchestrator.saga.LoginSaga;
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
    
    private final LoginSaga loginSaga;
    private final ObjectMapper objectMapper;

    public AuthController(LoginSaga loginSaga, ObjectMapper objectMapper) {
        this.loginSaga = loginSaga;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            String response = loginSaga.execute(loginRequest);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"success\":false, \"message\":\"Login request timed out\"}");
            }

            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") ? responseJson.get("success").asBoolean() : true; // Assume success if field is missing for combined response

            if (success) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":false, \"message\":\"Error processing login request: " + e.getMessage() + "\"}");
        }
    }
}