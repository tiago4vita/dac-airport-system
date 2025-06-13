package airportsystem.msauth.consumer;

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
public class InativarUsuarioConsumer {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public InativarUsuarioConsumer(UsuarioRepository usuarioRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "auth.inativar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Mensagem vazia. A mensagem deve conter o código do usuário a ser inativado.");
            }
            
            JsonNode jsonNode = objectMapper.readTree(msg);
            
            // Verificar se o código foi fornecido na mensagem
            if (!jsonNode.has("codigo") || jsonNode.get("codigo").asText().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "O código do usuário deve ser fornecido na mensagem e não pode ser vazio");
            }
            
            String codigo = jsonNode.get("codigo").asText();
            
            // Inativar o usuário
            Usuario usuarioInativado = inativarUsuario(codigo);
            
            System.out.println("Usuário inativado via RabbitMQ: (" + usuarioInativado.getCodigo() + ")");

            // Prepare successful response
            response.put("success", true);
            response.put("codigo", usuarioInativado.getCodigo());
            response.put("login", usuarioInativado.getLogin());
            response.put("tipo", usuarioInativado.getTipo());
            response.put("ativo", usuarioInativado.isAtivo());
            response.put("message", "Usuário inativado com sucesso");

        } catch (ResponseStatusException e) {
            System.err.println("Erro ao inativar usuário: " + e.getMessage());

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
            rabbitTemplate.convertAndSend("retorno", responseJson);
            System.out.println("Resposta enviada para a fila retorno: " + responseJson);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
        }
    }

    @Transactional
    public Usuario inativarUsuario(String codigo) {
        // Buscar o usuário pelo código
        Usuario usuario = usuarioRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário com código " + codigo + " não encontrado"));

        // Verificar se o usuário já está inativo
        if (!usuario.isAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Usuário com código " + codigo + " já está inativo");
        }

        // Inativar o usuário
        usuario.setAtivo(false);
        
        // Alternativamente, podemos usar o método desativar() do modelo Usuario
        // usuario = usuario.desativar();
        
        // Salvar o usuário inativado
        return usuarioRepository.save(usuario);
    }
}