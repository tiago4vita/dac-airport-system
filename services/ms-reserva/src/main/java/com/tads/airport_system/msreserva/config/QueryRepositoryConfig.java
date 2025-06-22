package com.tads.airport_system.msreserva.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for query repositories in CQRS pattern.
 * This configuration handles repositories that work with the query database.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.tads.airport_system.msreserva.repository",
        includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.REGEX,
                pattern = ".*ReservaViewRepository"
        ),
        entityManagerFactoryRef = "queryEntityManagerFactory",
        transactionManagerRef = "queryTransactionManager"
)
public class QueryRepositoryConfig {
}
