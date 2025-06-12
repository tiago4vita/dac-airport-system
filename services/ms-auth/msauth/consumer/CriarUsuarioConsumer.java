package airportsystem.msauth.consumer;

import airportsystem.msauth.dto.UsuarioDTO;
import airportsystem.msauth.model.Usuario;
import airportsystem.msauth.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
public class CriarUsuarioConsumer {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CriarUsuarioConsumer(UsuarioRepository usuarioRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "auth.criar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Mensagem vazia. A mensagem deve conter os dados do usuário, incluindo o código.");
            }
            
            UsuarioDTO usuarioDTO = objectMapper.readValue(msg, UsuarioDTO.class);
            
            // Verificar se o código foi fornecido na mensagem
            if (usuarioDTO.getCodigo() == null || usuarioDTO.getCodigo().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "O código do usuário deve ser fornecido na mensagem e não pode ser vazio");
            }
            
            Usuario novoUsuario = createUsuario(usuarioDTO);
            System.out.println("Usuario criado via RabbitMQ: (" + novoUsuario.getLogin() + ") " + msg);

            // Prepare successful response
            response.put("success", true);
            response.put("usuario", novoUsuario);
            response.put("message", "Usuario criado com sucesso");

        } catch (ResponseStatusException e) {
            System.err.println("Erro ao criar usuario: " + e.getMessage());

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
    public Usuario createUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByCodigo(usuarioDTO.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Usuario com codigo " + usuarioDTO.getCodigo() + " já existe");
        }

        if (usuarioRepository.existsByLogin(usuarioDTO.getLogin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Usuario com email " + usuarioDTO.getLogin() + " já existe");
        }

        Usuario usuario = new Usuario(
                usuarioDTO.getCodigo(),
                usuarioDTO.getLogin(),
                usuarioDTO.getSenha(),
                usuarioDTO.getTipo(),
                true
        );

        return usuarioRepository.save(usuario);
    }
}