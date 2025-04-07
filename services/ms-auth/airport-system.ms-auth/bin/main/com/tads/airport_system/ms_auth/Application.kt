package com.tads.airport_system.ms_auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
	exclude = [
		DataSourceAutoConfiguration::class,
		HibernateJpaAutoConfiguration::class
	]
)
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
