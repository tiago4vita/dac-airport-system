

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody

@RestController

class CadastroController(
    private val saga: Saga
) {
    @PostMapping("/cadastro")
    fun cadastrarCliente(@RequestBody clienteCadastro: ClienteCadastro): ResponseEntity<String> {
        return try {
            val response = saga.executeSaga(clienteCadastro)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: ${e.message}")
        }
    }
}