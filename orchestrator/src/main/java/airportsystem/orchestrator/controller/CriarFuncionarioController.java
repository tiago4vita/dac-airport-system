package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.dto.FuncionarioRequestDTO;
import airportsystem.orchestrator.saga.CriarFuncionarioSaga;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin
public class CriarFuncionarioController {
    
    private final CriarFuncionarioSaga criarFuncionarioSaga;
    
    public CriarFuncionarioController(CriarFuncionarioSaga criarFuncionarioSaga) {
        this.criarFuncionarioSaga = criarFuncionarioSaga;
    }
    
    @PostMapping("/funcionario")
    public ResponseEntity<String> criarFuncionario(@RequestBody FuncionarioRequestDTO funcionarioRequest) {
        try {
            String response = criarFuncionarioSaga.execute(funcionarioRequest);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":false,\"message\":\"Error processing funcionario creation request: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}");
        }
    }
}
