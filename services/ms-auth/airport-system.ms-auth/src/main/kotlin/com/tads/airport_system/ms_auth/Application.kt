package com.tads.airport_system.ms_auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication(
	exclude = [
		DataSourceAutoConfiguration::class,
		HibernateJpaAutoConfiguration::class
	]
)
@EnableMongoRepositories
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
