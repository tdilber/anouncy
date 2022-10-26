package com.beyt.anouncy.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Import(ReloadableResourceBundleMessageSource.class)
public class MessageService {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String key) {
        return getMessage(key, Locale.forLanguageTag("tr"));
    }

    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
