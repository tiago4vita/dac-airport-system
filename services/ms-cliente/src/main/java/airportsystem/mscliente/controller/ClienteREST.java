package airportsystem.mscliente.controller;

import airportsystem.mscliente.model.Cliente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController("/api")
public class ClienteREST {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/clientes")
    ResponseEntity<?> enfileirarPessoa(@RequestBody Cliente p)
                throws JsonProcessingException {

        var json = objectMapper.writeValueAsString(p);

        rabbitTemplate.convertAndSend("retorno", json);

        return new ResponseEntity<>("Enfileirado: " + json, HttpStatus.OK);

    }
}
