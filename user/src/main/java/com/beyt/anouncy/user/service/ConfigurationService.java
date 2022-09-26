package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.entity.Configuration;
import com.beyt.anouncy.user.repository.ConfigurationRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConfigurationService implements ApplicationRunner {
    private final ConfigurationRepository configurationRepository;
    private Map<String, String> configurationMap = new HashMap<>();
    private Gson gson = new Gson();


    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        fetchAllConfigurations();
    }

    public String get(String key) {
        return configurationMap.get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String valueStr = configurationMap.get(key);
        return gson.fromJson(valueStr, clazz);
    }


    private void fetchAllConfigurations() {
        configurationMap = configurationRepository.findAll().stream().collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
    }
}
