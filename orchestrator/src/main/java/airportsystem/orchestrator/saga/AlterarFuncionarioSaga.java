package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.FuncionarioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlterarFuncionarioSaga {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AlterarFuncionarioSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute(FuncionarioRequestDTO funcionarioRequest) {
        try {
            // Step 1: Create funcionario (without senha)
            FuncionarioRequestDTO funcionarioWithoutSenha = new FuncionarioRequestDTO(
                    funcionarioRequest.getCpf(),
                    funcionarioRequest.getNome(),
                    funcionarioRequest.getEmail(),
                    funcionarioRequest.getTelefone(),
                    //funcionarioRequest.getSenha()
                    null // Don't send senha to ms-func
            );

            String funcionarioJsonPayload = objectMapper.writeValueAsString(funcionarioWithoutSenha);
            String funcionarioResponse = (String) rabbitTemplate.convertSendAndReceive("funcionario.atualizar", funcionarioJsonPayload);

            if (funcionarioResponse == null) {
                return "{\"success\":false,\"message\":\"Funcionario creation request timed out\",\"errorType\":\"TIMEOUT_ERROR\"}";
            }

            // Parse funcionario response
            JsonNode funcionarioResponseJson = objectMapper.readTree(funcionarioResponse);
            boolean funcionarioSuccess = funcionarioResponseJson.has("success") && funcionarioResponseJson.get("success").asBoolean();

            if (!funcionarioSuccess) {
                return funcionarioResponse; // Return the error from ms-func
            }

            // Extract funcionario codigo for compensation if needed
            String funcionarioCodigo = funcionarioRequest.getCpf();
            if (funcionarioResponseJson.has("funcionario") && funcionarioResponseJson.get("funcionario").has("codigo")) {
                funcionarioCodigo = funcionarioResponseJson.get("funcionario").get("codigo").asText();
            }

            // Both operations successful - return success response
            return "{\"success\":true,\"message\":\"Funcionario updated successfully\",\"funcionario\":" +
                    funcionarioResponseJson.get("funcionario").toString() + "}";

        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Error processing funcionario update: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
}
