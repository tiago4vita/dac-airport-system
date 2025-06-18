package airportsystem.msvoo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue vooRealizarQueue() { return new Queue("voo.realizar", false); }

    @Bean
    public Queue vooCriarQueue() { return new Queue("voo.criar", false); }

    @Bean
    public Queue vooBuscarQueue() { return new Queue("voo.buscar", false); }

    @Bean
    public Queue vooBuscarProximas48HorasQueue() { return new Queue("voo.buscar-proximas-48-horas", false); }

    @Bean
    public Queue vooBuscarDiaOrigemDestinoQueue() { return new Queue("voo.buscar-dia-origem-destino", false); }

    @Bean
    public Queue vooCancelarQueue() { return new Queue("voo.cancelar", false); }

    @Bean
    public Queue aeroportoBuscarTodosQueue() { return new Queue("aeroporto.buscar-todos", false); }

    @Bean
    public Queue retornoQueue() {
        return new Queue("retorno", false);
    }


    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }
}
