package com.tads.airport_system.ms_auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.tads.airport_system.ms_auth.repository"])
class MongoConfig : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String = "auth"
} 