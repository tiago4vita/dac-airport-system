package com.tads.airport_system.ms_auth.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.concurrent.TimeUnit

@Configuration
@EnableMongoRepositories(basePackages = ["com.tads.airport_system.ms_auth.repository"])
class MongoConfig : AbstractMongoClientConfiguration() {
    @Value("\${spring.data.mongodb.uri}")
    private lateinit var mongoUri: String

    override fun getDatabaseName(): String = "auth"
    
    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString(mongoUri)
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToSocketSettings { builder -> 
                builder.connectTimeout(5000, TimeUnit.MILLISECONDS)
            }
            .build()
        
        return MongoClients.create(mongoClientSettings)
    }
} 