package airportsystem.msvoo.consumer;

import airportsystem.msvoo.model.Voo;
import airportsystem.msvoo.model.Aeroporto;
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class CriarVooConsumer {

    private final VooRepository vooRepository;
    private final AeroportoRepository aeroportoRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CriarVooConsumer(VooRepository vooRepository, AeroportoRepository aeroportoRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.vooRepository = vooRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.aeroportoRepository = aeroportoRepository;
    }

    @RabbitListener(queues = "voo.criar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Mensagem vazia. A mensagem deve conter os dados do voo.");
            }

            JsonNode jsonNode = objectMapper.readTree(msg);

            String dateStr = jsonNode.get("data").asText();
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr);
            LocalDateTime dataHora = zonedDateTime.toLocalDateTime();

            String origem = jsonNode.get("codigo_aeroporto_origem").asText();
            String destino = jsonNode.get("codigo_aeroporto_destino").asText();
            double precoEmReais = jsonNode.get("valor_passagem").asDouble();
            int quantidadePoltronasTotal = jsonNode.get("quantidade_poltronas_total").asInt();
            int quantidadePoltronasOcupadas = jsonNode.get("quantidade_poltronas_ocupadas").asInt();

            if (!aeroportoRepository.existsByCodigo(origem)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Aeroporto de origem com código " + origem + " não encontrado");
            }

            if (!aeroportoRepository.existsByCodigo(destino)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aeroporto de destino com código " + destino + " não encontrado"); 
            }

            Voo voo = new Voo(dataHora, origem, destino, precoEmReais, quantidadePoltronasTotal, quantidadePoltronasOcupadas);
            vooRepository.save(voo);

            Aeroporto aeroportoOrigem = aeroportoRepository.findByCodigo(origem);
            Aeroporto aeroportoDestino = aeroportoRepository.findByCodigo(destino);

            Map<String, Object> aeroportoOrigemMap = new HashMap<>();
            aeroportoOrigemMap.put("codigo", aeroportoOrigem.getCodigo());
            aeroportoOrigemMap.put("nome", aeroportoOrigem.getNome());
            aeroportoOrigemMap.put("cidade", aeroportoOrigem.getCidade());
            aeroportoOrigemMap.put("uf", aeroportoOrigem.getUF());

            Map<String, Object> aeroportoDestinoMap = new HashMap<>();
            aeroportoDestinoMap.put("codigo", aeroportoDestino.getCodigo());
            aeroportoDestinoMap.put("nome", aeroportoDestino.getNome());
            aeroportoDestinoMap.put("cidade", aeroportoDestino.getCidade());
            aeroportoDestinoMap.put("uf", aeroportoDestino.getUF());

            // Format the datetime for the response
            String formattedDateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

            response.put("codigo", voo.getCodigo());
            response.put("data", formattedDateTime); // Use the formatted datetime
            response.put("valor_passagem", voo.getPrecoEmReais());
            response.put("quantidade_poltronas_total", voo.getQuantidadePoltronasTotal());
            response.put("quantidade_poltronas_ocupadas", voo.getQuantidadePoltronasOcupadas());
            response.put("estado", voo.getEstado().toString());
            response.put("aeroporto_origem", aeroportoOrigemMap);
            response.put("aeroporto_destino", aeroportoDestinoMap);
            response.put("success", true);

        } catch (ResponseStatusException e) {
            System.err.println("Erro ao criar voo: " + e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errorType", "VALIDATION_ERROR");
            response.put("statusCode", e.getStatusCode().value());

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