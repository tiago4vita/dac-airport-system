package airportsystem.mscliente.consumer;

import airportsystem.mscliente.model.Cliente;
import airportsystem.mscliente.repository.ClienteRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DeletarClienteConsumer {

    @Autowired
    private ClienteRepository clienteRepository;

    @RabbitListener(queues = "cliente.deletar")
    public void handleDeletarCliente(String message) {
        try {
            if (message == null || message.isEmpty()) {
                System.err.println("Mensagem vazia. A mensagem deve conter o código do cliente.");
                return;
            }
            
            System.out.println("Deletar cliente recebido via RabbitMQ: " + message);
            
            // The message should be just the cliente codigo as a string
            String codigo = message.trim();
            
            // Find the cliente by codigo
            Optional<Cliente> optionalCliente = clienteRepository.findById(codigo);
            
            if (optionalCliente.isEmpty()) {
                System.err.println("Cliente with codigo " + codigo + " not found for deletion");
                return;
            }
            
            Cliente cliente = optionalCliente.get();
            
            // Delete the cliente
            clienteRepository.delete(cliente);
            
            System.out.println("Cliente deleted successfully for compensation: " + codigo);
            
        } catch (Exception e) {
            System.err.println("Error deleting cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 