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
public class DeletarFuncionarioController {

    private final CriarFuncionarioSaga criarFuncionarioSaga;

    public DeletarFuncionarioController(CriarFuncionarioSaga criarFuncionarioSaga) {
        this.criarFuncionarioSaga = criarFuncionarioSaga;
    }

    @DeleteMapping("/funcionario/{id}")
    public ResponseEntity<String> deletarFuncionario(@RequestBody FuncionarioRequestDTO funcionarioRequest) {
        try {
            String response = DeletarFuncionarioSaga.execute(funcionarioRequest);
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
