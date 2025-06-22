package airportsystem.msauth.consumer;

import airportsystem.msauth.dto.UsuarioDTO;
import airportsystem.msauth.model.Usuario;
import airportsystem.msauth.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginConsumer {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public LoginConsumer(UsuarioRepository usuarioRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "auth.login")
    public String receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Mensagem vazia. A mensagem deve conter os dados do usuário, incluindo o login e senha.");
            }
            System.out.println("Login recebido via RabbitMQ: " + msg);

            JsonNode jsonNode = objectMapper.readTree(msg);

            String login = jsonNode.get("login").asText();
            String senha = jsonNode.get("senha").asText();

            // Validate login and senha
            Usuario usuario = usuarioRepository.findByLogin(login);
            if(usuario == null) {
                System.err.println("Erro ao validar login: Login incorreto");

                // Prepare error response
                response.put("success", false);
                response.put("message", "Erro ao validar login: Login incorreto");
                response.put("errorType", "VALIDATION_ERROR");
                response.put("statusCode", 401);
            } else if (!usuario.isAtivo()) {
                System.err.println("Erro ao logar: Usuário inativo");

                // Prepare error response
                response.put("success", false);
                response.put("message", "Erro ao logar: Usuário inativo");
                response.put("errorType", "INACTIVE_USER");
                response.put("statusCode", 401);
            } else {// Prepare successful response with required fields
                response.put("success", true);
                response.put("login", usuario.getLogin());
                response.put("senha", usuario.getSenha());
                response.put("codigo", usuario.getCodigo());
                response.put("tipo", usuario.getTipo());
                response.put("message", "Login realizado com sucesso");
            }
        } catch (ResponseStatusException e) {
            System.err.println("Erro ao validar login: " + e.getMessage());

            // Prepare error response
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errorType", "VALIDATION_ERROR");
            response.put("statusCode", e.getStatusCode().value());

        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar mensagem: " + e.getMessage());
            e.printStackTrace();

            // Prepare error response
            response.put("success", false);
            response.put("message", "Erro interno: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
        }

        // Send response to retorno queue
        try {
            String responseJson = objectMapper.writeValueAsString(response);
            return responseJson;
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            return e.getMessage();
        }
    }
}