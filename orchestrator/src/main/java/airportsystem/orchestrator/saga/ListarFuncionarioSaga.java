package airportsystem.orchestrator.saga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ListarFuncionarioSaga {

    private static final Logger logger = LoggerFactory.getLogger(ListarFuncionarioSaga.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ListarFuncionarioSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute() {
        try {
            // 1) payload vazio
            String requestJson = objectMapper.writeValueAsString(Collections.emptyMap());

            // 2) manda na fila "cliente.listar" e espera resposta
            String responseJson = (String) rabbitTemplate
                    .convertSendAndReceive("funcionario.listar", requestJson);

            if (responseJson == null) {
                throw new RuntimeException("Timeout ao buscar clientes");
            }

            JsonNode root = objectMapper.readTree(responseJson);

            // se já vier um array puro, devolve direto
            if (root.isArray()) {
                return responseJson;
            }

            // senão vem envelope { success:…, clientes:[…] }
            JsonNode successNode = root.get("success");
            if (successNode == null || !successNode.asBoolean()) {
                JsonNode msg = root.get("message");
                throw new RuntimeException(
                    msg != null ? msg.asText() : "Erro interno ao listar clientes"
                );
            }

            JsonNode clientesNode = root.get("funcionarios");
            if (clientesNode == null || !clientesNode.isArray()) {
                return "[]";
            }

            // desempacota só o array de clientes
            return objectMapper.writeValueAsString(clientesNode);

        } catch (Exception e) {
            logger.error("Erro em ListarClientesSaga", e);
            throw new RuntimeException("Erro interno ao listar clientes", e);
        }
    }

    // Se quiser manter este helper para futuros usos, tudo bem. Senão, pode removê-lo.
    @SuppressWarnings("unused")
    private String createError(String msg, String type) {
        try {
            return objectMapper
                .createObjectNode()
                .put("success", false)
                .put("message", msg)
                .put("errorType", type)
                .toString();
        } catch (Exception ex) {
            return "{\"success\":false,\"message\":\""+msg+"\",\"errorType\":\""+type+"\"}";
        }
    }
}
