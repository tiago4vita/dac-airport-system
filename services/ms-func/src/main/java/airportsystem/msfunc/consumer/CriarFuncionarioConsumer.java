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

@Component
public class CriarFuncionarioConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.criar")
    public String handleCriarFuncionario(String message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (message == null || message.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mensagem vazia. A mensagem deve conter os dados do funcionário.");
                response.put("errorType", "VALIDATION_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            System.out.println("Criar funcionario recebido via RabbitMQ: " + message);
            
            FuncionarioDTO funcionarioDTO = objectMapper.readValue(message, FuncionarioDTO.class);
            
            // Validate CPF format first
            if (!isValidCPFFormat(funcionarioDTO.getCpf())) {
                response.put("success", false);
                response.put("message", "CPF inválido: formato incorreto");
                response.put("errorType", "VALIDATION_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            // Check if funcionario already exists
            if (funcionarioRepository.existsByCpf(funcionarioDTO.getCpf())) {
                response.put("success", false);
                response.put("message", "Funcionario with CPF " + funcionarioDTO.getCpf() + " already exists");
                response.put("errorType", "DUPLICATE_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            if (funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
                response.put("success", false);
                response.put("message", "Funcionario with email " + funcionarioDTO.getEmail() + " already exists");
                response.put("errorType", "DUPLICATE_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            // Create new funcionario - always set ativo to true for new funcionarios
            Funcionario funcionario = new Funcionario(
                funcionarioDTO.getCpf(),
                funcionarioDTO.getNome(),
                funcionarioDTO.getEmail(),
                funcionarioDTO.getTelefone()
            );
            
            // Ensure ativo is always true for new funcionarios
            funcionario.setAtivo(true);
            funcionario.setDataCriacao(LocalDateTime.now());
            
            Funcionario savedFuncionario = funcionarioRepository.save(funcionario);
            
            // Create success response
            response.put("success", true);
            
            Map<String, Object> funcionarioData = new HashMap<>();
            funcionarioData.put("codigo", savedFuncionario.getCodigo());
            funcionarioData.put("cpf", savedFuncionario.getCpf());
            funcionarioData.put("nome", savedFuncionario.getNome());
            funcionarioData.put("email", savedFuncionario.getEmail());
            funcionarioData.put("telefone", savedFuncionario.getTelefone());
            funcionarioData.put("ativo", savedFuncionario.getAtivo());
            funcionarioData.put("dataCriacao", savedFuncionario.getDataCriacao());
            
            response.put("funcionario", funcionarioData);
            response.put("message", "Funcionario criado com sucesso");
            
            System.out.println("Funcionario created successfully: " + savedFuncionario.getCodigo());
            
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao processar JSON: " + e.getMessage());
            response.put("errorType", "JSON_ERROR");
        } catch (Exception e) {
            System.err.println("Error creating funcionario: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao criar funcionario: " + e.getMessage());
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
    
    private boolean isValidCPFFormat(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        
        // Remove non-digits
        String cleanCpf = cpf.replaceAll("\\D", "");
        
        // Check if it has 11 digits
        if (cleanCpf.length() != 11) {
            return false;
        }
        
        // Check if all digits are the same
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Validate CPF algorithm
        try {
            int[] digits = new int[11];
            for (int i = 0; i < 11; i++) {
                digits[i] = Integer.parseInt(cleanCpf.substring(i, i + 1));
            }

            // Calculate first check digit
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += digits[i] * (10 - i);
            }
            int remainder = sum % 11;
            int checkDigit1 = remainder < 2 ? 0 : 11 - remainder;

            if (digits[9] != checkDigit1) {
                return false;
            }

            // Calculate second check digit
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += digits[i] * (11 - i);
            }
            remainder = sum % 11;
            int checkDigit2 = remainder < 2 ? 0 : 11 - remainder;

            return digits[10] == checkDigit2;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 