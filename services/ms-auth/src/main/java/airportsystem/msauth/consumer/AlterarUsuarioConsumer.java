package airportsystem.msauth.consumer;

import airportsystem.msauth.model.Usuario;
import airportsystem.msauth.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.fasterxml.jackson.databind.JsonNode;
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
public class AlterarUsuarioConsumer {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public AlterarUsuarioConsumer(UsuarioRepository usuarioRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "auth.alterar")
    public void receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Mensagem vazia. A mensagem deve conter os dados do usuário, incluindo o código e o novo login.");
            }
            
            JsonNode jsonNode = objectMapper.readTree(msg);
            
            // Verificar se o código foi fornecido na mensagem
            if (!jsonNode.has("codigo") || jsonNode.get("codigo").asText().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "O código do usuário deve ser fornecido na mensagem e não pode ser vazio");
            }
            
            // Verificar se o login foi fornecido na mensagem
            if (!jsonNode.has("login") || jsonNode.get("login").asText().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "O login do usuário deve ser fornecido na mensagem e não pode ser vazio");
            }
            
            String codigo = jsonNode.get("codigo").asText();
            String novoLogin = jsonNode.get("login").asText();
            
            // Atualizar apenas o login do usuário
            Usuario usuarioAtualizado = atualizarLoginUsuario(codigo, novoLogin);
            
            System.out.println("Login do usuário atualizado via RabbitMQ: (" + usuarioAtualizado.getLogin() + ")");

            // Prepare successful response
            response.put("success", true);
            response.put("codigo", usuarioAtualizado.getCodigo());
            response.put("login", usuarioAtualizado.getLogin());
            response.put("tipo", usuarioAtualizado.getTipo());
            response.put("ativo", usuarioAtualizado.isAtivo());
            response.put("message", "Login do usuário atualizado com sucesso");

        } catch (ResponseStatusException e) {
            System.err.println("Erro ao atualizar login do usuário: " + e.getMessage());

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
    public Usuario atualizarLoginUsuario(String codigo, String novoLogin) {
        // Verificar se o novo login é um email válido
        Usuario usuarioTemp = new Usuario();
        if (!usuarioTemp.isEmailValid(novoLogin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O login deve ser um endereço de email válido");
        }
        
        // Buscar o usuário pelo código
        Usuario usuario = usuarioRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário com código " + codigo + " não encontrado"));

        // Verificar se o novo login já existe para outro usuário
        if (!usuario.getLogin().equals(novoLogin) && 
            usuarioRepository.existsByLogin(novoLogin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Usuário com email " + novoLogin + " já existe");
        }

        // Atualizar apenas o login do usuário
        usuario.setLogin(novoLogin);
        
        // Salvar o usuário atualizado
        return usuarioRepository.save(usuario);
    }
}