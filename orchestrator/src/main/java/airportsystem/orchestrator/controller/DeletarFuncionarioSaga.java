package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.saga.DeletarFuncionarioSaga;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin
public class DeletarFuncionarioController {

    private final DeletarFuncionarioSaga deletarFuncionarioSaga;

    public DeletarFuncionarioController(DeletarFuncionarioSaga deletarFuncionarioSaga) {
        this.deletarFuncionarioSaga = deletarFuncionarioSaga;
    }

    @DeleteMapping(
      value    = "/funcionarios/{codigoFuncionario}",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deletarFuncionario(
            @PathVariable("codigoFuncionario") String codigo) {

        String resultado = deletarFuncionarioSaga.execute(codigo);

        // Se veio success:false, devolve BAD_REQUEST, senão OK
        HttpStatus status = resultado.contains("\"success\":false")
            ? HttpStatus.BAD_REQUEST
            : HttpStatus.OK;

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resultado);
    }
}
