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
class Saga(
    private val rabbit: RabbitUtils,
    @Qualifier("sagaRepository") private val sagaRepository: SagaRepository,
    private val exchange: DirectExchange,
    private val rabbitTemplate: RabbitTemplate
) {

    suspend fun executeSaga(clienteCadastro: ClienteCadastro): String = coroutineScope {
            val gson = Gson()
            val requestCliente = async { rabbitTemplate.convertAndSend(exchange.name, "routingkeyclient", gson.toJson(clienteCadastro)) }
            val responseCliente = requestCliente.await()

            val cliente = gson.fromJson(responseCliente, ClienteOutputDTO::class.java)   
            val inputCadastro = ClienteCadastro(cliente.nome, cliente.cpf, cliente.email, cliente.telefone, cliente.endereco)
        
            processResponse(responseCliente, inputCadastro)
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