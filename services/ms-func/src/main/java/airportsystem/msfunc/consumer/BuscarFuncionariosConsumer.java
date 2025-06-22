package airportsystem.msfunc.consumer;

import airportsystem.msfunc.dto.FuncionarioDTO;
import airportsystem.msfunc.model.Funcionario;
import airportsystem.msfunc.repository.FuncionarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BuscarFuncionariosConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.buscar")
    public String handleBuscarFuncionarios(String message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (message == null || message.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mensagem vazia.");
                response.put("errorType", "VALIDATION_ERROR");
                return objectMapper.writeValueAsString(response);
            }
            
            System.out.println("Buscar funcionarios recebido via RabbitMQ: " + message);
            
            // Get all ativo funcionarios
            List<Funcionario> funcionarios = funcionarioRepository.findByAtivoTrue();
            
            // Convert to DTOs
            List<Map<String, Object>> funcionarioDTOs = funcionarios.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            
            // Create success response
            response.put("success", true);
            response.put("funcionarios", funcionarioDTOs);
            response.put("message", "Funcionarios encontrados: " + funcionarioDTOs.size());
            
            System.out.println("Found " + funcionarioDTOs.size() + " ativo funcionarios");
            
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao processar JSON: " + e.getMessage());
            response.put("errorType", "JSON_ERROR");
        } catch (Exception e) {
            System.err.println("Error searching funcionarios: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno ao buscar funcionarios: " + e.getMessage());
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

    private Map<String, Object> convertToMap(Funcionario funcionario) {
        Map<String, Object> funcionarioMap = new HashMap<>();
        funcionarioMap.put("codigo", funcionario.getCodigo());
        funcionarioMap.put("cpf", funcionario.getCpf());
        funcionarioMap.put("nome", funcionario.getNome());
        funcionarioMap.put("email", funcionario.getEmail());
        funcionarioMap.put("telefone", funcionario.getTelefone());
        funcionarioMap.put("dataCriacao", funcionario.getDataCriacao());
        funcionarioMap.put("dataAtualizacao", funcionario.getDataAtualizacao());
        funcionarioMap.put("ativo", funcionario.getAtivo());
        return funcionarioMap;
    }
} 