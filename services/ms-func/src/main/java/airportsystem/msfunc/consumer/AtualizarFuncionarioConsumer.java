package airportsystem.msfunc.consumer;

import airportsystem.msfunc.dto.FuncionarioDTO;
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
public class AtualizarFuncionarioConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.atualizar")
    public String handleAtualizarFuncionario(String message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (message == null || message.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mensagem vazia. A mensagem deve conter os dados do funcionário.");
                response.put("errorType", "VALIDATION_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            System.out.println("Atualizar funcionario recebido via RabbitMQ: " + message);
            
            FuncionarioDTO funcionarioDTO = objectMapper.readValue(message, FuncionarioDTO.class);
            
            // Find the funcionario by codigo
            Optional<Funcionario> optionalFuncionario = funcionarioRepository.findById(funcionarioDTO.getCodigo());
            
            if (optionalFuncionario.isEmpty()) {
                response.put("success", false);
                response.put("message", "Funcionario with codigo " + funcionarioDTO.getCodigo() + " not found");
                response.put("errorType", "NOT_FOUND_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            Funcionario funcionario = optionalFuncionario.get();
            
            // Update fields
            funcionario.setNome(funcionarioDTO.getNome());
            funcionario.setEmail(funcionarioDTO.getEmail());
            funcionario.setTelefone(funcionarioDTO.getTelefone());
            funcionario.setAtivo(funcionarioDTO.getAtivo());
            funcionario.setDataAtualizacao(LocalDateTime.now());
            
            // Check if CPF is being changed and if it already exists
            if (!funcionario.getCpf().equals(funcionarioDTO.getCpf())) {
                if (funcionarioRepository.existsByCpf(funcionarioDTO.getCpf())) {
                    response.put("success", false);
                    response.put("message", "CPF " + funcionarioDTO.getCpf() + " already exists");
                    response.put("errorType", "DUPLICATE_ERROR");
                    return objectMapper.writeValueAsString(response);
                }
                funcionario.setCpf(funcionarioDTO.getCpf());
            }
            
            // Check if email is being changed and if it already exists
            if (!funcionario.getEmail().equals(funcionarioDTO.getEmail())) {
                if (funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
                    response.put("success", false);
                    response.put("message", "Email " + funcionarioDTO.getEmail() + " already exists");
                    response.put("errorType", "DUPLICATE_ERROR");
                    return objectMapper.writeValueAsString(response);
                }
            }
            
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
            response.put("message", "Funcionario atualizado com sucesso");
            
            System.out.println("Funcionario updated successfully: " + updatedFuncionario.getCodigo());
            
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao processar JSON: " + e.getMessage());
            response.put("errorType", "JSON_ERROR");
        } catch (Exception e) {
            System.err.println("Error updating funcionario: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao atualizar funcionario: " + e.getMessage());
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