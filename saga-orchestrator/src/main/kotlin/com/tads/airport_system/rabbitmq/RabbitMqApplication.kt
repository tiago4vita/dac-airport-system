package com.tads.airport_system.rabbitmq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.boot.CommandLineRunner

@SpringBootApplication
class RabbitMqApplication

fun main(args: Array<String>) {
	runApplication<RabbitMqApplication>(*args)
	println("RabbitMQ application started")
}
