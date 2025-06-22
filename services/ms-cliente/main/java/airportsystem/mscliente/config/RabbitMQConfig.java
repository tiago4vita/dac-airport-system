package airportsystem.mscliente.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue clienteCriarQueue() { return new Queue("cliente.criar", true); }

    @Bean
    public Queue clienteBuscarQueue() { return new Queue("cliente.buscar", true); }

    @Bean
    public Queue clienteSomarMilhasQueue() { return new Queue("cliente.somar-milhas", true); }

    @Bean
    public Queue clienteGastarMilhasQueue() { return new Queue("cliente.gastar-milhas", true); }

    @Bean
    public Queue clienteBuscarExtratoMilhasQueue() { return new Queue("cliente.buscar-extrato-milhas", true); }


    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
