package com.beyt.anouncy.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anouncy", ignoreUnknownFields = false)
public class AnouncyApplicationProperties {
    Password password = new Password();

    public static class Password {
        Salt salt = new Salt();

        public static class Salt {
            public String user = null;
            public String anonymous = null;
        }
    }
}
