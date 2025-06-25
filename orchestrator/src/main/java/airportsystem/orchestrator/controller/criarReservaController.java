package airportsystem.orchestrator.controller;

import airportsystem.orchestrator.saga.criarReservaSaga;
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

public class criarReservaController {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public criarReservaController(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/reservas")
    public ResponseEntity<String> criarReserva(@RequestBody ReservaDTO vooRequest) {
        try{
            Map<String, String> reservaPayload = new HashMap<>();
            reservaPayload.put("codigo_voo", reservaRequest.getCodigo_voo());
            reservaPayload.put("codigo_cliente", reservaRequest.getCodigo_cliente());
            reservaPayload.put("valor", String.valueOf(reservaRequest.getValor()));
            reservaPayload.put("quantidade_poltronas", String.valueOf(reservaRequest.getQuantidade_poltronas()));
            reservaPayload.put("milhas_utilizadas", String.valueOf(reservaRequest.getMilhas_utilizadas()));
            reservaPayload.put("codigo_aeroporto_origem", reservaRequest.getCodigo_aeroporto_origem());
            reservaPayload.put("codigo_aeroporto_destino", reservaRequest.getCodigo_aeroporto_destino());

            String jsonPayload = objectMapper.writeValueAsString(reservaPayload);

            String response = (String) rabbitTemplate.convertSendAndReceive("reserva.efetuar", jsonPayload);
            f (response == null) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body("Voo request timed out");
            }

            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") && responseJson.get("success").asBoolean();

            if (success) {
                return ResponseEntity.ok(responseJson.toString());
            } else {
                String message = responseJson.has("message") ?
                        responseJson.get("message").asText() : "Reservation creation failed";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing request: " + e.getMessage());
        }
    }
}
