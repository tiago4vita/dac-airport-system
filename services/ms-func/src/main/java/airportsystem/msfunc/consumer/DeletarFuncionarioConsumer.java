package airportsystem.msfunc.consumer;

import airportsystem.msfunc.model.Funcionario;
import airportsystem.msfunc.repository.FuncionarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DeletarFuncionarioConsumer {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.deletar")
    public void handleDeletarFuncionario(String message) {
        try {
            if (message == null || message.isEmpty()) {
                System.err.println("Mensagem vazia. A mensagem deve conter o código do funcionário.");
                return;
            }
            
            System.out.println("Deletar funcionario recebido via RabbitMQ: " + message);
            
            // The message should be just the funcionario codigo as a string
            String codigo = message.trim();
            
            // Find the funcionario by codigo
            Optional<Funcionario> optionalFuncionario = funcionarioRepository.findById(codigo);
            
            if (optionalFuncionario.isEmpty()) {
                System.err.println("Funcionario with codigo " + codigo + " not found for deletion");
                return;
            }
            
            Funcionario funcionario = optionalFuncionario.get();
            
            // Delete the funcionario
            funcionarioRepository.delete(funcionario);
            
            System.out.println("Funcionario deleted successfully for compensation: " + codigo);
            
        } catch (Exception e) {
            System.err.println("Error deleting funcionario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
