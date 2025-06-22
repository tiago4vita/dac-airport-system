package airportsystem.msfunc.consumer;

import airportsystem.msfunc.model.Funcionario;
import airportsystem.msfunc.repository.FuncionarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InativarFuncionarioConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.inativar")
    public String handleInativarFuncionario(String message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (message == null || message.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mensagem vazia. A mensagem deve conter o código do funcionário.");
                response.put("errorType", "VALIDATION_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            System.out.println("Inativar funcionario recebido via RabbitMQ: " + message);
            
            // Assuming the message contains the funcionario codigo
            String codigo = objectMapper.readValue(message, String.class);
            
            // Find the funcionario by codigo
            Optional<Funcionario> optionalFuncionario = funcionarioRepository.findById(codigo);
            
            if (optionalFuncionario.isEmpty()) {
                response.put("success", false);
                response.put("message", "Funcionario with codigo " + codigo + " not found");
                response.put("errorType", "NOT_FOUND_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            Funcionario funcionario = optionalFuncionario.get();
            
            // Check if already inactive
            if (!funcionario.getAtivo()) {
                response.put("success", false);
                response.put("message", "Funcionario " + codigo + " is already inactive");
                response.put("errorType", "ALREADY_INACTIVE_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            // Inactivate the funcionario
            funcionario.setAtivo(false);
            funcionario.setDataAtualizacao(LocalDateTime.now());
            
            Funcionario updatedFuncionario = funcionarioRepository.save(funcionario);
            
            // Create success response
            response.put("success", true);
            
            Map<String, Object> funcionarioData = new HashMap<>();
            funcionarioData.put("codigo", updatedFuncionario.getCodigo());
            funcionarioData.put("cpf", updatedFuncionario.getCpf());
            funcionarioData.put("nome", updatedFuncionario.getNome());
            funcionarioData.put("email", updatedFuncionario.getEmail());
            funcionarioData.put("telefone", updatedFuncionario.getTelefone());
            funcionarioData.put("ativo", updatedFuncionario.getAtivo());
            funcionarioData.put("dataCriacao", updatedFuncionario.getDataCriacao());
            funcionarioData.put("dataAtualizacao", updatedFuncionario.getDataAtualizacao());
            
            response.put("funcionario", funcionarioData);
            response.put("message", "Funcionario inativado com sucesso");
            
            System.out.println("Funcionario inactivated successfully: " + updatedFuncionario.getCodigo());
            
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao processar JSON: " + e.getMessage());
            response.put("errorType", "JSON_ERROR");
        } catch (Exception e) {
            System.err.println("Error inactivating funcionario: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao inativar funcionario: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }
        
        // Send response
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            return "{\"success\":false,\"message\":\"Erro crítico ao processar JSON\",\"errorType\":\"CRITICAL_ERROR\"}";
        }
    }
} 