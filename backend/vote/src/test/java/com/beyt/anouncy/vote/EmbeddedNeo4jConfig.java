package com.beyt.anouncy.vote;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.neo4j.Neo4jAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.Neo4jLabsPlugin;

@TestConfiguration
@ImportAutoConfiguration({
        Neo4jAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class
})
@EnableTransactionManagement
@EnableNeo4jRepositories(considerNestedRepositories = true)
public abstract class EmbeddedNeo4jConfig {

    static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.4.15")
            .withLabsPlugins(Neo4jLabsPlugin.APOC)
            .withReuse(true);

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {

        neo4jContainer.start();

        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
    }
}
