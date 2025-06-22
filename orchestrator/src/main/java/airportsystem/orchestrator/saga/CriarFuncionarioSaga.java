package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.FuncionarioRequestDTO;
import airportsystem.orchestrator.dto.UsuarioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class CriarFuncionarioSaga {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CriarFuncionarioSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
    
    public String execute(FuncionarioRequestDTO funcionarioRequest) {
        String funcionarioCodigo = null;
        
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
            String funcionarioResponse = (String) rabbitTemplate.convertSendAndReceive("funcionario.criar", funcionarioJsonPayload);
            
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
            
            // Step 2: Create usuario
            UsuarioRequestDTO usuarioRequest = new UsuarioRequestDTO(
                funcionarioCodigo,
                funcionarioRequest.getEmail(), // Use email as login
                funcionarioRequest.getSenha(),
                "FUNCIONARIO"
            );
            
            String usuarioJsonPayload = objectMapper.writeValueAsString(usuarioRequest);
            String usuarioResponse = (String) rabbitTemplate.convertSendAndReceive("auth.criar", usuarioJsonPayload);
            
            if (usuarioResponse == null) {
                // Compensate: Delete funcionario
                if (funcionarioCodigo != null) {
                    rabbitTemplate.convertAndSend("funcionario.deletar", funcionarioCodigo);
                }
                return "{\"success\":false,\"message\":\"Usuario creation request timed out\",\"errorType\":\"TIMEOUT_ERROR\"}";
            }
            
            // Parse usuario response
            JsonNode usuarioResponseJson = objectMapper.readTree(usuarioResponse);
            boolean usuarioSuccess = usuarioResponseJson.has("success") && usuarioResponseJson.get("success").asBoolean();
            
            if (!usuarioSuccess) {
                // Compensate: Delete funcionario
                if (funcionarioCodigo != null) {
                    rabbitTemplate.convertAndSend("funcionario.deletar", funcionarioCodigo);
                }
                return usuarioResponse; // Return the error from ms-auth
            }
            
            // Both operations successful - return success response
            return "{\"success\":true,\"message\":\"Funcionario and usuario created successfully\",\"funcionario\":" + 
                   funcionarioResponseJson.get("funcionario").toString() + 
                   ",\"usuario\":" + usuarioResponseJson.get("usuario").toString() + "}";
            
        } catch (Exception e) {
            // Compensate: Delete funcionario if it was created
            if (funcionarioCodigo != null) {
                try {
                    rabbitTemplate.convertAndSend("funcionario.deletar", funcionarioCodigo);
                } catch (Exception compensationException) {
                    // Log compensation failure but don't throw
                    System.err.println("Failed to compensate funcionario deletion: " + compensationException.getMessage());
                }
            }
            
            return "{\"success\":false,\"message\":\"Error processing funcionario creation: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
}
