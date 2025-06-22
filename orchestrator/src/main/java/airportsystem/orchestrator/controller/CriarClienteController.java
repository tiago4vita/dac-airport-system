package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.dto.ClienteRequestDTO;
import airportsystem.orchestrator.saga.CriarClienteSaga;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin
public class CriarClienteController {

    private final CriarClienteSaga criarClienteSaga;
    private final ObjectMapper objectMapper;

    public CriarClienteController(CriarClienteSaga criarClienteSaga, ObjectMapper objectMapper) {
        this.criarClienteSaga = criarClienteSaga;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/clientes")
    public ResponseEntity<String> criarCliente(@RequestBody ClienteRequestDTO clienteRequest) {
        try {
            String result = criarClienteSaga.execute(clienteRequest);
            
            // Try to parse the response to determine if it's a success or error
            try {
                JsonNode responseJson = objectMapper.readTree(result);
                
                // If the response has a "success" field, it's an error response
                if (responseJson.has("success")) {
                    boolean success = responseJson.get("success").asBoolean();
                    if (success) {
                        // Success response with wrapper - return 200 with cliente data
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(result);
                    } else {
                        // Error response - return appropriate error status
                        String errorType = responseJson.has("errorType") ? 
                            responseJson.get("errorType").asText() : "GENERAL_ERROR";
                        
                        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                        if ("TIMEOUT_ERROR".equals(errorType)) {
                            status = HttpStatus.GATEWAY_TIMEOUT;
                        } else if ("VALIDATION_ERROR".equals(errorType)) {
                            status = HttpStatus.BAD_REQUEST;
                        } else if ("NOT_FOUND_ERROR".equals(errorType)) {
                            status = HttpStatus.NOT_FOUND;
                        } else if ("CONFLICT_ERROR".equals(errorType)) {
                            status = HttpStatus.CONFLICT;
                        }
                        
                        return ResponseEntity.status(status)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(result);
                    }
                } else {
                    // Direct cliente data response (success) - return 201 Created
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(result);
                }
            } catch (Exception e) {
                // If we can't parse the response, assume it's a direct cliente data response
                return ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":false,\"message\":\"Error processing cliente creation request: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}");
        }
    }
} 