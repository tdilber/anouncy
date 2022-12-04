package com.beyt.anouncy.announce.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.transaction.TransactionManager;

@Configuration
@EnableNeo4jRepositories(
        basePackages = "com.beyt.anouncy",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Neo4jRepository.class)
)
@EnableElasticsearchRepositories("com.beyt.anouncy.common.repository.elasticsearch")
public class DatabaseConfiguration {

    @Bean(Neo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
    public TransactionManager transactionManager(Driver driver, DatabaseSelectionProvider databaseNameProvider) {
        return new Neo4jTransactionManager(driver, databaseNameProvider);
    }
}
