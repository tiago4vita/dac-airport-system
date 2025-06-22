package airportsystem.msvoo.consumer;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import airportsystem.msvoo.model.Aeroporto;
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
public class BuscarVooConsumer {

    private final VooRepository vooRepository;
    private final AeroportoRepository aeroportoRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BuscarVooConsumer(VooRepository vooRepository, AeroportoRepository aeroportoRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.vooRepository = vooRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.aeroportoRepository = aeroportoRepository;
    }

    @RabbitListener(queues = "voo.buscar")
    public String receiveMessage(String msg) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("BuscarVooConsumer received message: '" + msg + "'");

            JsonNode rootNode = objectMapper.readTree(msg);
            String codigoVoo = rootNode.get("codigo").asText();
            System.out.println("Looking for flight with code: '" + codigoVoo + "'");

            Voo voo = vooRepository.findByCodigo(codigoVoo);

            if (voo != null) {
                Aeroporto origem = aeroportoRepository.findByCodigo(voo.getOrigem());
                Aeroporto destino = aeroportoRepository.findByCodigo(voo.getDestino());

                System.out.println("Voo encontrado via RabbitMQ: (" + voo.getCodigo() + ") de " + voo.getOrigem() + " para " + voo.getDestino());
                response.put("success", true);
                response.put("message", "Voo encontrado com sucesso");

                Map<String, Object> vooMap = new HashMap<>();
                vooMap.put("codigo", voo.getCodigo());
                vooMap.put("dataHora", voo.getDataHora());
                vooMap.put("origem", origem);
                vooMap.put("destino", destino);
                vooMap.put("precoEmReais", voo.getPrecoEmReais());
                vooMap.put("quantidadePoltronasTotal", voo.getQuantidadePoltronasTotal());
                vooMap.put("quantidadePoltronasOcupadas", voo.getQuantidadePoltronasOcupadas());
                vooMap.put("estado", voo.getEstado());

                response.put("voo", vooMap);
            } else {
                response.put("success", false);
                response.put("message", "Voo não encontrado com o código: " + codigoVoo);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao buscar voo: " + e.getMessage());
        }

        String responseJson = objectMapper.writeValueAsString(response);
        System.out.println("Sending response: " + responseJson);
        return responseJson;
    }
}