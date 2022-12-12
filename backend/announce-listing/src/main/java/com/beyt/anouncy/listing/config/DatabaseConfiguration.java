package com.beyt.anouncy.listing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories("com.beyt.anouncy.common.repository.elasticsearch")
public class DatabaseConfiguration {

}
