package com.tads.airport_system.ms_saga_orchestrator.saga

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class SagaRabbitMQConsumer(
    private val sagaManager: UserRegistrationSagaManager
) {
    private val logger = LoggerFactory.getLogger(SagaRabbitMQConsumer::class.java)

    @RabbitListener(queues = ["saga-orchestrator-reply-queue"])
    fun receiveReply(message: Map<String, Any>) {
        try {
            logger.info("Orchestrator received reply: $message")

            val sagaId = message["sagaId"] as? String
            val eventType = message["eventType"] as? String
            val payload = message["payload"] as? Map<String, Any>

            if (sagaId == null || eventType == null || payload == null) {
                logger.error("Invalid reply message structure received: $message")
                return
            }

            when (eventType) {
                "AUTH_USER_CREATED" -> sagaManager.handleAuthUserCreated(sagaId, payload)
                "AUTH_USER_CREATION_FAILED" -> sagaManager.handleAuthUserCreationFailed(sagaId, payload)

                else -> logger.warn("Saga Orchestrator: Unknown event type received: $eventType for saga $sagaId")
            }
        } catch (e: Exception) {
            logger.error("Saga Orchestrator: Error processing reply message: ${e.message}. Message: $message", e)
        }
    }
}