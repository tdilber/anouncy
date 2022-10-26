package com.beyt.anouncy.common.config;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

@Configuration
public class MessageConfig {

    @Autowired
    private Environment environment;

    @Bean
    public ResourceBundleMessageSource messageSource() {
        String baseName = environment.getProperty("spring.messages.basename");
        String encoding = environment.getProperty("spring.messages.encoding");

        if (Strings.isBlank(baseName)) {
            baseName = "i18n/message";
        }
        if (Strings.isBlank(encoding)) {
            encoding = "UTF-8";
        }

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        messageSource.setDefaultEncoding(encoding);
        messageSource.setBundleClassLoader(MessageConfig.class.getClassLoader());
        messageSource.setAlwaysUseMessageFormat(true);
        return messageSource;
    }
}
