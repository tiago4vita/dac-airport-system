package airportsystem.msfunc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue funcionarioCriarQueue() { 
        return new Queue("funcionario.criar", false); 
    }

    @Bean
    public Queue funcionarioBuscarQueue() { 
        return new Queue("funcionario.buscar", false); 
    }

    @Bean
    public Queue funcionarioAtualizarQueue() { 
        return new Queue("funcionario.atualizar", false); 
    }

    @Bean
    public Queue funcionarioInativarQueue() { 
        return new Queue("funcionario.inativar", false); 
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
