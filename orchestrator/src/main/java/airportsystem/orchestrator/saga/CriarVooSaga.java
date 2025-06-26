package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.VooDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;

@Service
public class CriarVooSaga {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CriarVooSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> execute(VooDTO vooRequest){
        try{
            Map<String, String> voopayload = new HashMap<>();
            voopayload.put("data", vooRequest.getData().toString());
            voopayload.put("valor_passagem", String.valueOf(vooRequest.getValor_passagem()));
            voopayload.put("quantidade_poltronas_total", String.valueOf(vooRequest.getQuantidade_poltronas_total()));
            voopayload.put("quantidade_poltronas_ocupadas", String.valueOf(vooRequest.getQuantidade_poltronas_ocupadas()));
            voopayload.put("codigo_aeroporto_origem", vooRequest.getCodigo_aeroporto_origem());
            voopayload.put("codigo_aeroporto_destino", vooRequest.getCodigo_aeroporto_destino());

            String jsonPayload = objectMapper.writeValueAsString(voopayload);

            String response = (String) rabbitTemplate.convertSendAndReceive("voo.criar", jsonPayload);

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
                        responseJson.get("message").asText() : "Flight creation failed";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing request: " + e.getMessage());
        }
        }
    }
