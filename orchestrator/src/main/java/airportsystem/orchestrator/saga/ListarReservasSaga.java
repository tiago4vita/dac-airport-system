package airportsystem.orchestrator.saga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListarReservasSaga {

    private static final Logger logger = LoggerFactory.getLogger(ListarReservasSaga.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ListarReservasSaga(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String execute(String codigoCliente) {
        try {
            logger.info("Starting ListarReservasSaga for cliente: {}", codigoCliente);

            // Step 1: Get all reservations for the client
            logger.debug("Sending request to reserva.listar queue with cliente code: '{}'", codigoCliente);
            String reservasResponseJson = (String) rabbitTemplate.convertSendAndReceive("reserva.listar", codigoCliente);
            logger.debug("Received response from reserva.listar: {}", reservasResponseJson);
            
            if (reservasResponseJson == null) {
                logger.error("Timeout waiting for response from reserva.listar queue");
                return createErrorResponse("Failed to get reservations from ms-reserva", "TIMEOUT_ERROR");
            }

            JsonNode reservasResponse = objectMapper.readTree(reservasResponseJson);
            logger.debug("Parsed reservas response: {}", reservasResponse.toString());
            
            if (!reservasResponse.get("success").asBoolean()) {
                logger.error("Error from ms-reserva while listing reservations: {}", reservasResponse.get("message").asText());
                return reservasResponseJson;
            }

            ArrayNode reservasArray = (ArrayNode) reservasResponse.get("reservas");
            logger.info("Found {} reservations for cliente {}", reservasArray != null ? reservasArray.size() : 0, codigoCliente);
            
            if (reservasArray == null || reservasArray.isEmpty()) {
                logger.info("No reservations found for cliente {}, returning empty array", codigoCliente);
                return "[]"; // Return empty list if no reservations
            }

            // Step 2: For each reservation, fetch flight details
            List<JsonNode> fullReservas = new ArrayList<>();
            for (JsonNode reservaNode : reservasArray) {
                String vooId = reservaNode.get("vooId").asText();
                logger.debug("Processing reservation with vooId: {}", vooId);
                
                ObjectNode vooRequestPayload = objectMapper.createObjectNode();
                vooRequestPayload.put("codigo", vooId);
                String vooRequestJson = objectMapper.writeValueAsString(vooRequestPayload);
                logger.debug("Sending request to voo.buscar queue: {}", vooRequestJson);

                String vooResponseJson = (String) rabbitTemplate.convertSendAndReceive("voo.buscar", vooRequestJson);
                logger.debug("Received response from voo.buscar: {}", vooResponseJson);
                
                if (vooResponseJson == null) {
                    logger.warn("Timeout fetching flight details for vooId: {}", vooId);
                    continue; 
                }
                JsonNode vooResponse = objectMapper.readTree(vooResponseJson);

                if (vooResponse.get("success").asBoolean()) {
                    logger.debug("Successfully fetched flight details for vooId: {}", vooId);
                    ObjectNode mergedReserva = formatResponse(reservaNode, vooResponse);
                    fullReservas.add(mergedReserva);
                } else {
                    logger.warn("Failed to fetch flight details for vooId: {}. Reason: {}", vooId, vooResponse.get("message").asText());
                }
            }
            
            String finalResponse = objectMapper.writeValueAsString(fullReservas);
            logger.info("Returning {} complete reservations for cliente {}", fullReservas.size(), codigoCliente);
            return finalResponse;

        } catch (Exception e) {
            logger.error("Error in ListarReservasSaga: {}", e.getMessage(), e);
            return createErrorResponse("Internal Server Error in ListarReservasSaga", "INTERNAL_ERROR");
        }
    }

    private ObjectNode formatResponse(JsonNode reservaNode, JsonNode vooNode) {
        ObjectNode finalNode = objectMapper.createObjectNode();
        finalNode.put("codigo", reservaNode.get("id").asText());
        finalNode.put("data", reservaNode.get("dataHoraRes").asText());
        finalNode.put("valor", reservaNode.get("valor").asDouble());
        finalNode.put("milhas_utilizadas", reservaNode.get("milhasUtilizadas").asInt());
        finalNode.put("quantidade_poltronas", reservaNode.get("quantidadePoltronas").asInt());
        finalNode.put("codigo_cliente", reservaNode.get("clienteId").asText());

        if (reservaNode.has("estado") && reservaNode.get("estado").has("codigoEstado")) {
            finalNode.put("estado", reservaNode.get("estado").get("codigoEstado").asText());
        } else {
             finalNode.put("estado", "UNKNOWN");
        }

        ObjectNode vooDetails = (ObjectNode) vooNode.deepCopy();
        vooDetails.remove("success");
        finalNode.set("voo", vooDetails);

        return finalNode;
    }

    private String createErrorResponse(String message, String errorType) {
        try {
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("success", false);
            errorResponse.put("message", message);
            errorResponse.put("errorType", errorType);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Failed to serialize error response\",\"errorType\":\"INTERNAL_ERROR\"}";
        }
    }
} 