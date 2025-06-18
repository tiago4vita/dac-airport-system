package airportsystem.msvoo.consumer;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BuscarVoosDiaOrigemDestinoConsumer {

    private final VooRepository vooRepository;
    private final AeroportoRepository aeroportoRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BuscarVoosDiaOrigemDestinoConsumer(VooRepository vooRepository, AeroportoRepository aeroportoRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.vooRepository = vooRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.aeroportoRepository = aeroportoRepository;
    }

    @RabbitListener(queues = "voo.buscar-dia-origem-destino")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Mensagem vazia. A mensagem deve conter a data, origem e destino.");
            }

            JsonNode jsonNode = objectMapper.readTree(msg);

            // Expecting the date, origin, and destination in the message
            LocalDate data = LocalDate.parse(jsonNode.get("data").asText());
            String origem = jsonNode.get("origem").asText();
            String destino = jsonNode.get("destino").asText();

            // Fetch the Voos by data, origem, and destino
            List<Voo> voos = vooRepository.findByDataHoraBetweenAndOrigemAndDestino(
                    data.atStartOfDay(), data.atTime(23, 59, 59), origem, destino);
            if (voos.isEmpty()) {
                response.put("success", true);
                response.put("message", "Nenhum voo encontrado para o período especificado.");
                response.put("voos", new ArrayList<>());
            } else {
                List<Map<String, Object>> voosList = voos.stream()
                        .map(voo -> {
                            Map<String, Object> vooMap = new HashMap<>();
                            vooMap.put("codigo", voo.getCodigo());

                            // Convert LocalDateTime to ZonedDateTime with the appropriate time zone
                            ZonedDateTime zonedDateTime = voo.getDataHora().atZone(ZoneId.of("America/Sao_Paulo"));
                            String formattedDateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
                            vooMap.put("data", formattedDateTime);

                            vooMap.put("valor_passagem", voo.getPrecoEmReais());
                            vooMap.put("quantidade_poltronas_total", voo.getQuantidadePoltronasTotal());
                            vooMap.put("quantidade_poltronas_ocupadas", voo.getQuantidadePoltronasOcupadas());
                            vooMap.put("estado", voo.getEstado().toString());

                            Aeroporto aeroportoOrigem = aeroportoRepository.findByCodigo(voo.getOrigem());
                            Aeroporto aeroportoDestino = aeroportoRepository.findByCodigo(voo.getDestino());

                            Map<String, String> aeroportoOrigemMap = new HashMap<>();
                            aeroportoOrigemMap.put("codigo", aeroportoOrigem.getCodigo());
                            aeroportoOrigemMap.put("nome", aeroportoOrigem.getNome());
                            aeroportoOrigemMap.put("cidade", aeroportoOrigem.getCidade());
                            aeroportoOrigemMap.put("uf", aeroportoOrigem.getUF());
                            vooMap.put("aeroporto_origem", aeroportoOrigemMap);

                            Map<String, String> aeroportoDestinoMap = new HashMap<>();
                            aeroportoDestinoMap.put("codigo", aeroportoDestino.getCodigo());
                            aeroportoDestinoMap.put("nome", aeroportoDestino.getNome());
                            aeroportoDestinoMap.put("cidade", aeroportoDestino.getCidade());
                            aeroportoDestinoMap.put("uf", aeroportoDestino.getUF());
                            vooMap.put("aeroporto_destino", aeroportoDestinoMap);

                            return vooMap;
                        })
                        .collect(Collectors.toList());

                response.put("data", data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                response.put("origem", origem);
                response.put("destino", destino);
                response.put("voos", voosList);
                response.put("success", true);
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