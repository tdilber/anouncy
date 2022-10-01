package com.beyt.anouncy.user.helper;

import com.beyt.anouncy.user.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashHelper {
    private final ConfigurationService configurationService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PasswordEncoder sCryptPasswordEncoder;
    private final Environment environment;

    public boolean check(HashType type, String password, String exitingHash) {
        String key = type.getConfigKey();
        String propertySalt = environment.getProperty(key);
        String configSalt = configurationService.get(key);
        if (Strings.isBlank(configSalt) || Strings.isBlank(propertySalt)) {
            throw new IllegalStateException();
        }

        return getEncoder(propertySalt, configSalt).matches(salted(propertySalt, configSalt, password), exitingHash);
    }

    public String hash(HashType type, String password) {
        String key = type.getConfigKey();
        String propertySalt = environment.getProperty(key);
        String configSalt = configurationService.get(key);
        if (Strings.isBlank(configSalt) || Strings.isBlank(propertySalt)) {
            throw new IllegalStateException();
        }

        return getEncoder(propertySalt, configSalt).encode(salted(propertySalt, configSalt, password));
    }

    private String salted(String propertySalt, String configSalt, String password) {
        return new StringBuilder().append(propertySalt).append(password).append(configSalt).toString();
    }

    private PasswordEncoder getEncoder(String propertySalt, String configSalt) {
        int value1 = propertySalt.charAt(0);
        int value2 = configSalt.charAt(0);
        return (value1 + value2) % 2 == 1 ? bCryptPasswordEncoder : sCryptPasswordEncoder;
    }

    public enum HashType {
        USER("anouncy.password.salt.user"),
        ANONYMOUS_USER("anouncy.password.salt.anonymous"),
        SESSION("anouncy.password.salt.session");

        private final String configKey;

        HashType(String configPath) {
            this.configKey = configPath;
        }

        public String getConfigKey() {
            return configKey;
        }
    }
}
