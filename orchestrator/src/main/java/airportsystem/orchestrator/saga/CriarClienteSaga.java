package airportsystem.orchestrator.saga;

import airportsystem.orchestrator.dto.ClienteRequestDTO;
import airportsystem.orchestrator.dto.UsuarioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class CriarClienteSaga {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CriarClienteSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
    
    public String execute(ClienteRequestDTO clienteRequest) {
        String clienteCodigo = null;
        
        try {
            // Step 1: Create cliente (without senha)
            ClienteRequestDTO clienteWithoutSenha = new ClienteRequestDTO(
                clienteRequest.getCpf(),
                clienteRequest.getEmail(),
                clienteRequest.getNome(),
                clienteRequest.getSaldoMilhas(),
                clienteRequest.getEndereco(),
                null // Don't send senha to ms-cliente
            );
            
            String clienteJsonPayload = objectMapper.writeValueAsString(clienteWithoutSenha);
            String clienteResponse = (String) rabbitTemplate.convertSendAndReceive("cliente.criar", clienteJsonPayload);
            
            if (clienteResponse == null) {
                return "{\"success\":false,\"message\":\"Cliente creation request timed out\",\"errorType\":\"TIMEOUT_ERROR\"}";
            }
            
            // Parse cliente response
            JsonNode clienteResponseJson = objectMapper.readTree(clienteResponse);
            boolean clienteSuccess = clienteResponseJson.has("success") && clienteResponseJson.get("success").asBoolean();
            
            if (!clienteSuccess) {
                return clienteResponse; // Return the error from ms-cliente
            }
            
            // Extract cliente codigo for compensation if needed
            if (clienteResponseJson.has("cliente") && clienteResponseJson.get("cliente").has("codigo")) {
                clienteCodigo = clienteResponseJson.get("cliente").get("codigo").asText();
            }
            
            // Step 2: Create usuario
            UsuarioRequestDTO usuarioRequest = new UsuarioRequestDTO(
                clienteCodigo,
                clienteRequest.getEmail(), // Use email as login
                clienteRequest.getSenha(),
                "CLIENTE"
            );
            
            String usuarioJsonPayload = objectMapper.writeValueAsString(usuarioRequest);
            String usuarioResponse = (String) rabbitTemplate.convertSendAndReceive("auth.criar", usuarioJsonPayload);
            
            if (usuarioResponse == null) {
                // Compensate: Delete cliente
                if (clienteCodigo != null) {
                    rabbitTemplate.convertAndSend("cliente.deletar", clienteCodigo);
                }
                return "{\"success\":false,\"message\":\"Usuario creation request timed out\",\"errorType\":\"TIMEOUT_ERROR\"}";
            }
            
            // Parse usuario response
            JsonNode usuarioResponseJson = objectMapper.readTree(usuarioResponse);
            boolean usuarioSuccess = usuarioResponseJson.has("success") && usuarioResponseJson.get("success").asBoolean();
            
            if (!usuarioSuccess) {
                // Compensate: Delete cliente
                if (clienteCodigo != null) {
                    rabbitTemplate.convertAndSend("cliente.deletar", clienteCodigo);
                }
                return usuarioResponse; // Return the error from ms-auth
            }
            
            // Both operations successful - return success response
            return "{\"success\":true,\"message\":\"Cliente and usuario created successfully\",\"cliente\":" + 
                   clienteResponseJson.get("cliente").toString() + 
                   ",\"usuario\":" + usuarioResponseJson.get("usuario").toString() + "}";
            
        } catch (Exception e) {
            // Compensate: Delete cliente if it was created
            if (clienteCodigo != null) {
                try {
                    rabbitTemplate.convertAndSend("cliente.deletar", clienteCodigo);
                } catch (Exception compensationException) {
                    // Log compensation failure but don't throw
                    System.err.println("Failed to compensate cliente deletion: " + compensationException.getMessage());
                }
            }
            
            return "{\"success\":false,\"message\":\"Error processing cliente creation: " + e.getMessage() + "\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
} 