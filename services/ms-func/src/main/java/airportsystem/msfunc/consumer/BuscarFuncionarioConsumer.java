package airportsystem.msfunc.consumer;

import airportsystem.msfunc.model.Funcionario;
import airportsystem.msfunc.repository.FuncionarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BuscarFuncionarioConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.buscar")
    @Transactional(readOnly = true)
    public String handleBuscarFuncionario(String codigo) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("Funcionario code cannot be empty.");
            }

            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(codigo.trim());

            if (funcionarioOpt.isPresent()) {
                response.put("success", true);
                response.put("funcionario", funcionarioOpt.get());
            } else {
                response.put("success", false);
                response.put("message", "Funcionario with code " + codigo + " not found.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error finding funcionario: " + e.getMessage());
        }

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{\"success\":false,\"message\":\"Failed to serialize response\"}";
        }
    }
} 