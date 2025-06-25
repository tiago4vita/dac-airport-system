package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.FuncionarioRequestDTO;
import airportsystem.orchestrator.dto.UsuarioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;

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
            if (funcionarioResponseJson.has("funcionario") && funcionarioResponseJson.get("funcionario").has("codigo")) {
                funcionarioCodigo = funcionarioResponseJson.get("funcionario").get("codigo").asText();
            }
            
            // Both operations successful - return success response
            return "{\"success\":true,\"message\":\"Funcionario updated successfully\",\"funcionario\":" +
                jsonResponse.get("funcionario").toString() + "}";
            
        } catch (Exception e) {
            // Compensate: Delete funcionario if it was created
            return "{\"success\":false,\"message\":\"Error processing funcionario update: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}";
            }
            

        }
    }

