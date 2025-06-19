package airportsystem.msvoo.consumer;

import airportsystem.msvoo.model.Voo;
import airportsystem.msvoo.repository.VooRepository;
import airportsystem.msvoo.repository.AeroportoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class CancelarVooConsumer {

    private final VooRepository vooRepository;
    private final AeroportoRepository aeroportoRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CancelarVooConsumer(VooRepository vooRepository, AeroportoRepository aeroportoRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.vooRepository = vooRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.aeroportoRepository = aeroportoRepository;
    }

    @RabbitListener(queues = "voo.cancelar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Mensagem vazia. A mensagem deve conter o codigo do voo.");
            }

            JsonNode jsonNode = objectMapper.readTree(msg);

            // Expecting only the flight code in the message
            String codigo = jsonNode.get("codigo").asText();

            // Fetch the Voo by codigo
            Voo voo = vooRepository.findByCodigo(codigo);
            if (voo == null) {
                response.put("success", false);
                response.put("message", "Voo não encontrado para o código: " + codigo);
            } else if (voo.getEstado() != "CONFIRMADO") { 
                response.put("success", false);
                response.put("message", "Voo não está com status CONFIRMADO.");
            } else {
                
                voo.setEstado(Voo.EstadoVoo.CANCELADO);
                vooRepository.save(voo);

                response.put("success", true);
                response.put("message", "Voo cancelado com sucesso.");
                response.put("codigo", voo.getCodigo());
                response.put("estado", voo.getEstado());
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar mensagem: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Erro interno: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }

        try {
            String responseJson = objectMapper.writeValueAsString(response);
            rabbitTemplate.convertAndSend("retorno", responseJson);
            System.out.println("Resposta enviada para a fila retorno: " + responseJson);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
        }
    }
}