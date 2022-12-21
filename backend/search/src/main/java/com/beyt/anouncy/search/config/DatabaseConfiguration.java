package com.beyt.anouncy.search.config;

import com.beyt.anouncy.search.helper.ElasticSearchHelper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@Configuration
@EnableElasticsearchRepositories("com.beyt.anouncy.search.controller")
public class DatabaseConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String url;
    @Value("${spring.elasticsearch.username}")
    private String username;
    @Value("${spring.elasticsearch.password}")
    private String password;

    @Bean
    @Profile({"test", "prod"})
    public RestClient createSimpleElasticClient() throws Exception {
        return ElasticSearchHelper.createRestClient(url, username, password);
    }
}
