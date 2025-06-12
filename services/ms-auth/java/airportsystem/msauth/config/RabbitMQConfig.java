package airportsystem.msauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue clienteCriarQueue() { return new Queue("auth.login", false); }

    @Bean
    public Queue clienteBuscarQueue() { return new Queue("auth.criar", false); }

    @Bean
    public Queue clienteSomarMilhasQueue() { return new Queue("auth.alterar", false); }

    @Bean
    public Queue retornoQueue() {
        return new Queue("retorno", false);
    }


    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
