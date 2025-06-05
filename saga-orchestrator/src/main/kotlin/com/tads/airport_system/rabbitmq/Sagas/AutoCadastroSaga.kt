package com.tads.airport_system.sagas

// import com.tads.airport_system.rabbitmq.RabbitMqApplication
// import com.tads.airport_system.rabbitmq.RabbitUtils
import org.springframework.amqp.rabbit.connection.RabbitUtils
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import async.coroutines.CoroutineScope



@Service
class AutoCadastroSaga(
    private val rabbit: RabbitUtils,
    @Qualifier("sagaRepository") private val sagaRepository: SagaRepository,
    private val exchange: DirectExchange,
    private val rabbitTemplate: RabbitTemplate
) {

    suspend fun executeSaga(clienteCadastro: ClienteCadastro): String = coroutineScope {
            val gson = Gson()
            val requestCliente = async { rabbitTemplate.convertAndSend(exchange.name, "cliente", gson.toJson(clienteCadastro)) }
            val responseCliente = requestCliente.await()

            val cliente = gson.fromJson(responseCliente, ClienteOutputDTO::class.java)   
            val inputCadastro = UsuarioInputDTO(cliente.codigo, cliente.email, cliente.senha, UsuarioRole.CLIENTE))
        
            // val requestAuth = async { rabbitTemplate.convertAndSend(exchange.name, "auth", gson.toJson(inputCadastro))}
            // val responseAuth = requestAuth.await()
            // processResponse(responseCliente, inputCadastro)
        }

    private suspend fun asyncSendAndReceive(
        exchange: DirectExchange,
        routingkey: String,
        message: String
    ): String {
        return withContext(Dispatchers.IO) {
            rabbitTemplate.convertSendAndReceive(exchange, routingkey, message) as String
        }

    }

    private fun processResponse(response: String, inputCadastro: UsuarioInputDTO): String {
        val gson = Gson()
        val cliente = gson.fromJson(response, ClienteOutputDTO::class.java)
        val usuario = UsuarioInputDTO(cliente.codigo, cliente.email, cliente.senha, UsuarioRole.CLIENTE)
        return usuario
    }








    //PARTE PARA O SERVICE DE CLIENTE
    // suspend fun executeSaga(clienteCadastro: ClienteCadastro): String = coroutineScope {
    //     val gson = Gson()
    //     val requestCliente = async { rabbitTemplate.convertAndSend(exchange.name, "routingkeyclient", gson.toJson(clienteCadastro)) }
    //     val responseCliente = requestCliente.await()
    // }

    // private suspend fun asyncSendAndReceive(
    //     exchange: String,
    //     routingKey: String,
    //     message: String
    // ): String {
    //     return withContext(Dispatchers.IO) {
    //         rabbitTemplate.convertSendAndReceive(exchange, routingKey, message) as String
    //     }
    // }
}