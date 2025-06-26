package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.dto.FuncionarioRequestDTO;
import airportsystem.orchestrator.saga.AlterarFuncionarioSaga;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AlterarFuncionarioController {

    private final AlterarFuncionarioSaga alterarFuncionarioSaga;
    private final ObjectMapper objectMapper;

    public AlterarFuncionarioController(AlterarFuncionarioSaga alterarFuncionarioSaga, ObjectMapper objectMapper) {
        this.alterarFuncionarioSaga = alterarFuncionarioSaga;
        this.objectMapper = objectMapper;
    }

    @PutMapping("/funcionarios/{codigoFuncionario}")
    public ResponseEntity<String> alterarFuncionario(@PathVariable String codigoFuncionario,@RequestBody FuncionarioRequestDTO funcionarioRequest) {
        try {
            //funcionarioRequest.setCpf(codigoFuncionario);
            String response = alterarFuncionarioSaga.execute(funcionarioRequest);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"success\":false, \"message\":\"Funcionario request timed out\"}");
            }

            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") ? responseJson.get("success").asBoolean() : true;

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
                    .body("{\"success\":false, \"message\":\"Error processing update request: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}
