package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.saga.ListarReservasSaga;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ListarReservasController {

    private final ListarReservasSaga listarReservasSaga;
    private final ObjectMapper objectMapper;

    public ListarReservasController(ListarReservasSaga listarReservasSaga, ObjectMapper objectMapper) {
        this.listarReservasSaga = listarReservasSaga;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/clientes/{codigoCliente}/reservas")
    public ResponseEntity<String> listarReservas(@PathVariable String codigoCliente) {
        try {
            String result = listarReservasSaga.execute(codigoCliente);
            JsonNode resultJson = objectMapper.readTree(result);

            if (resultJson.isObject() && resultJson.has("success") && !resultJson.get("success").asBoolean()) {
                // It's an error from the saga
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(result);
            }

            // It's a successful response (array of reservations)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":false,\"message\":\"Error processing reservations list request: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}");
        }
    }
} 