package com.beyt.anouncy.user.helper;

import com.beyt.anouncy.user.service.ConfigurationService;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashHelper {
    private final ConfigurationService configurationService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;

    public boolean check(HashType type, String password, String exitingHash) {
        String key = type.getConfigKey();
        String propertySalt = environment.getProperty(key);
        String configSalt = configurationService.get(key);
        if (Strings.isBlank(configSalt) || Strings.isBlank(propertySalt)) {
            throw new IllegalStateException();
        }

        return getEncoder(type).matches(salted(propertySalt, configSalt, password), exitingHash);
    }

    public String hash(HashType type, String password) {
        String key = type.getConfigKey();
        String propertySalt = environment.getProperty(key);
        String configSalt = configurationService.get(key);
        if (Strings.isBlank(configSalt) || Strings.isBlank(propertySalt)) {
            throw new IllegalStateException();
        }

        return encode(type, salted(propertySalt, configSalt, password));
    }

    private String salted(String propertySalt, String configSalt, String password) {
        return new StringBuilder().append(propertySalt).append(password).append(configSalt).toString();
    }

    private PasswordEncoder getEncoder(HashType hashType) {
        return switch (hashType.getHashingTool()) {
            case "bCryptPasswordEncoder" -> bCryptPasswordEncoder;
            default -> throw new IllegalStateException();
        };
    }

    private String encode(HashType hashType, String password) {
        return switch (hashType.getHashingTool()) {
            case "bCryptPasswordEncoder" -> bCryptPasswordEncoder.encode(password);
            case "sha512" -> Hashing.sha512().hashString(password, StandardCharsets.UTF_8).toString();
            default -> throw new IllegalStateException();
        };
    }

    public enum HashType {
        USER("anouncy.password.salt.user", "bCryptPasswordEncoder"),
        ANONYMOUS_USER("anouncy.password.salt.anonymous", "sha512"),
        SESSION("anouncy.password.salt.session", "sha512");

        private final String configKey;
        private final String hashingTool;

        HashType(String configPath, String hashingTool) {
            this.configKey = configPath;
            this.hashingTool = hashingTool;
        }

        public String getConfigKey() {
            return configKey;
        }

        public String getHashingTool() {
            return hashingTool;
        }
    }
}
