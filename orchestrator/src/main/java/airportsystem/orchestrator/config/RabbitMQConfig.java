package airportsystem.orchestrator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue retornoQueue() {
        return new Queue("retorno", true);
    }
    
    @Bean
    public Queue funcionarioCriarQueue() {
        return new Queue("funcionario.criar", false);
    }
    
    @Bean
    public Queue funcionarioDeletarQueue() {
        return new Queue("funcionario.deletar", false);
    }
    
    @Bean
    public Queue authCriarQueue() {
        return new Queue("auth.criar", false);
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
