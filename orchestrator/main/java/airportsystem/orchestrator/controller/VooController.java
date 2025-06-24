package airportsystem.orchestrator.dto;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;


@RestController
@CrossOrigin

public class VooController{
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public VooController(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/voos")
    public ResponseEntity<String> criarVoo(@RequestBody VooDTO vooRequest) {
        try {
            Map<String, String> voopayload = new HashMap<>();
            voopayload.put("data", vooRequest.getData().toString());
            voopayload.put("valor_passagem", vooRequest.getValorPassagem().toString());
            voopayload.put("quantidade_poltronas_total", String.valueOf(vooRequest.getQuantidadePoltronasTotal()));
            voopayload.put("quantidade_poltronas_ocupadas", String.valueOf(vooRequest.getQuantidadePoltronasOcupadas()));
            voopayload.put("codigo_aeroporto_origem", vooRequest.getCodigoAeroportoOrigem());
            voopayload.put("codigo_aeroporto_destino", vooRequest.getCodigoAeroportoDestino());

            String jsonPayload = objectMapper.writeValueAsString(voopayload);


            String response = (String) rabbitTemplate.convertSendAndReceive("voo.criar", jsonPayload);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body("Voo request timed out");
            }

            JsonNode responseJson = objectMapper.readTree(response);
            boolean success = responseJson.has("success") && responseJson.get("success").asBoolean();
            if (success) {
                return ResponseEntity.ok(responseJson);
            } else {
                String message = responseJson.has("message") ?
                        responseJson.get("message").asText() : "Flight creation failed";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
            }
        catch (Exception e) {
                return ResponseEntity.internalServerError()
                        .body("Error processing request: " + e.getMessage());
            }
        }
    }