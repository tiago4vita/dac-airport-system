package airportsystem.msauth.consumer;

import airportsystem.msauth.dto.UsuarioDTO;
import airportsystem.msauth.model.Usuario;
import airportsystem.msauth.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @Autowired
    public CriarUsuarioConsumer(UsuarioRepository usuarioRepository, ObjectMapper objectMapper) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "auth.criar")
    public String receiveMessage(String msg) throws JsonMappingException, JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        try {
            if (msg == null || msg.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Mensagem vazia. A mensagem deve conter os dados do usuário, incluindo o código.");
            }
            
            System.out.println("Criar usuario recebido via RabbitMQ: " + msg);
            
            // Parse the message as a Map first to handle the string tipo field
            Map<String, Object> messageMap = objectMapper.readValue(msg, Map.class);
            
            // Debug: Print all fields from the message
            System.out.println("Message fields:");
            for (Map.Entry<String, Object> entry : messageMap.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
            
            // Create UsuarioDTO manually to handle tipo conversion
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setCodigo((String) messageMap.get("codigo"));
            usuarioDTO.setLogin((String) messageMap.get("login"));
            usuarioDTO.setSenha((String) messageMap.get("senha"));
            
            // Debug: Print DTO values
            System.out.println("DTO values:");
            System.out.println("  codigo = " + usuarioDTO.getCodigo());
            System.out.println("  login = " + usuarioDTO.getLogin());
            System.out.println("  senha = " + (usuarioDTO.getSenha() != null ? "[HIDDEN]" : "null"));
            
            // Convert string tipo to enum
            String tipoString = (String) messageMap.get("tipo");
            if ("FUNCIONARIO".equals(tipoString)) {
                usuarioDTO.setTipo(Usuario.TipoUsuario.FUNCIONARIO);
            } else if ("CLIENTE".equals(tipoString)) {
                usuarioDTO.setTipo(Usuario.TipoUsuario.CLIENTE);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Tipo de usuário inválido. Deve ser 'FUNCIONARIO' ou 'CLIENTE'");
            }
            
            System.out.println("  tipo = " + usuarioDTO.getTipo());
            
            // Verificar se o código foi fornecido na mensagem
            if (usuarioDTO.getCodigo() == null || usuarioDTO.getCodigo().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "O código do usuário deve ser fornecido na mensagem e não pode ser vazio");
            }
            
            // Verificar se o login foi fornecido na mensagem
            if (usuarioDTO.getLogin() == null || usuarioDTO.getLogin().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "O login do usuário deve ser fornecido na mensagem e não pode ser vazio");
            }
            
            Usuario novoUsuario = createUsuario(usuarioDTO);
            System.out.println("Usuario criado via RabbitMQ: (" + novoUsuario.getLogin() + ") " + msg);

            // Prepare successful response
            response.put("success", true);
            
            Map<String, Object> usuarioData = new HashMap<>();
            usuarioData.put("codigo", novoUsuario.getCodigo());
            usuarioData.put("login", novoUsuario.getLogin());
            usuarioData.put("tipo", novoUsuario.getTipo());
            usuarioData.put("ativo", novoUsuario.isAtivo());
            
            response.put("usuario", usuarioData);
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

        // Return response directly
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter resposta para JSON: " + e.getMessage());
            return "{\"success\":false,\"message\":\"Erro crítico ao processar JSON\",\"errorType\":\"CRITICAL_ERROR\"}";
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

        // Debug: Print what we're about to save
        System.out.println("Creating Usuario with:");
        System.out.println("  codigo = " + usuarioDTO.getCodigo());
        System.out.println("  login = " + usuarioDTO.getLogin());
        System.out.println("  senha = " + (usuarioDTO.getSenha() != null ? "[HIDDEN]" : "null"));
        System.out.println("  tipo = " + usuarioDTO.getTipo());

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