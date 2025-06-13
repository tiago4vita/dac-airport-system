package airportsystem.msauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue usuarioLoginQueue() { return new Queue("auth.login", false); }

    @Bean
    public Queue usuarioCriarQueue() { return new Queue("auth.criar", false); }

    @Bean
    public Queue usuarioAlterarQueue() { return new Queue("auth.alterar", false); }

    @Bean
    public Queue usuarioInativarQueue() { return new Queue("auth.inativar", false); }

    @Bean
    public Queue retornoQueue() {
        return new Queue("retorno", false);
    }


    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
