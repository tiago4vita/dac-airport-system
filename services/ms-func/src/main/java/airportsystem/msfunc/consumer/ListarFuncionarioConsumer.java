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
import java.util.List;
import java.util.Map;

@Component
public class ListarFuncionarioConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.listar")
    @Transactional(readOnly = true)
    public String handleListarFuncionarios(String message) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Funcionario> funcionarios = funcionarioRepository.findAll();

            response.put("success", true);
            response.put("funcionarios", funcionarios);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error listing funcionarios: " + e.getMessage());
        }

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{\"success\":false,\"message\":\"Failed to serialize response\"}";
        }
    }
}
