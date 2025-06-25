package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.saga.ListarFuncionarioSaga;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ListarFuncionarioController {
    private final ListarFuncionarioSaga saga;
    private final ObjectMapper mapper;

    public ListarFuncionarioController(ListarFuncionarioSaga saga, ObjectMapper mapper) {
        this.saga   = saga;
        this.mapper = mapper;
    }

    @GetMapping(value = "/funcionarios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listar() {
        try {
            String result = saga.execute();
            JsonNode root = mapper.readTree(result);
            // se for objeto de erro
            if (root.isObject() && root.has("success") && !root.get("success").asBoolean()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
            // senão é array de clientes
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            String err = "{\"success\":false,\"message\":\"Erro ao processar listar clientes\",\"errorType\":\"INTERNAL_ERROR\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
