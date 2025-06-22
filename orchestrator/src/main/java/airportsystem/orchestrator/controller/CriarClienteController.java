package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.dto.ClienteRequestDTO;
import airportsystem.orchestrator.saga.CriarClienteSaga;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CriarClienteController {

    private final CriarClienteSaga criarClienteSaga;

    public CriarClienteController(CriarClienteSaga criarClienteSaga) {
        this.criarClienteSaga = criarClienteSaga;
    }

    @PostMapping("/clientes")
    public ResponseEntity<String> criarCliente(@RequestBody ClienteRequestDTO clienteRequest) {
        String result = criarClienteSaga.execute(clienteRequest);
        return ResponseEntity.ok(result);
    }
} 