package airportsystem.msvoo.consumer;

import airportsystem.msvoo.model.Aeroporto;
import airportsystem.msvoo.repository.AeroportoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BuscarTodosAeroportosConsumer {

    private final AeroportoRepository aeroportoRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BuscarTodosAeroportosConsumer(AeroportoRepository aeroportoRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.aeroportoRepository = aeroportoRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "aeroporto.buscar-todos")
    public void receiveMessage() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Aeroporto> aeroportos = aeroportoRepository.findAll();

            List<Map<String, String>> aeroportosList = aeroportos.stream()
                    .map(aeroporto -> {
                        Map<String, String> aeroportoMap = new HashMap<>();
                        aeroportoMap.put("codigo", aeroporto.getCodigo());
                        aeroportoMap.put("nome", aeroporto.getNome());
                        aeroportoMap.put("cidade", aeroporto.getCidade());
                        aeroportoMap.put("uf", aeroporto.getUF());
                        return aeroportoMap;
                    })
                    .collect(Collectors.toList());

            response.put("aeroportos", aeroportosList);
            response.put("success", true);

        } catch (Exception e) {
            System.err.println("Erro ao buscar aeroportos: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Erro interno: " + e.getMessage());
        }

        try {
            String responseJson = objectMapper.writeValueAsString(response);
            rabbitTemplate.convertAndSend("retorno", responseJson);
            System.out.println("Resposta enviada para a fila retorno: " + responseJson);
        } catch (Exception e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
        }
    }
}
