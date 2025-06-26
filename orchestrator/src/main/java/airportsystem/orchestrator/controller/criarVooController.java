package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.saga.CriarVooSaga;
import airportsystem.orchestrator.dto.VooDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class criarVooController {

    private final CriarVooSaga criarVooSaga;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public criarVooController(CriarVooSaga criarVooSaga,
                              RabbitTemplate rabbitTemplate,
                              ObjectMapper objectMapper) {
        this.criarVooSaga = criarVooSaga;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/voos")
    public ResponseEntity<String> criarVoo(@RequestBody VooDTO vooRequest) {
        try {
            ResponseEntity<String> response = criarVooSaga.execute(vooRequest);
            return response;
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":false,\"message\":\"Error processing voo creation request: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}");
        }
    }

    @GetMapping("/voo/{id}")
    public ResponseEntity<String> buscarVoo(@PathVariable String id) {
        try {
            String response = (String) rabbitTemplate.convertSendAndReceive("voo.buscar", id);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body("Voo request timed out");
            }

            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") && responseJson.get("success").asBoolean();

            if (success) {
                return ResponseEntity.ok(responseJson.toString());
            } else {
                String message = responseJson.has("message") ?
                        responseJson.get("message").asText() : "Flight not found";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing request: " + e.getMessage());
        }
    }
}
