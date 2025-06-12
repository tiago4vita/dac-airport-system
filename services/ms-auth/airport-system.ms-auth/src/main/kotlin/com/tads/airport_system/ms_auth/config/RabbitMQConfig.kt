package com.tads.airport_system.ms_auth.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    
    @Bean
    fun authServiceQueue(): Queue {
        return Queue("auth-service-queue", true)
    }

    @Bean
    fun authServiceExchange(): DirectExchange{
        return DirectExchange("auth")
    }

    @Bean
    fun binding(
        authServiceQueue: DirectExchange,
        authServiceExchange: Queue
    ):Binding{
        return BindingBuilder.bind(authServiceQueue).to(authServiceExchange).with("auth")
    }

    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }
    
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jsonMessageConverter()
        return rabbitTemplate
    }
} 