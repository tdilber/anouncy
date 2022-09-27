package com.beyt.anouncy.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "anouncy", ignoreUnknownFields = false)
public class AnouncyApplicationProperties {
    Password password = new Password();
    JwtToken jwtToken = new JwtToken();

    @Data
    public static class Password {
        Salt salt = new Salt();

        public static class Salt {
            public String user = null;
            public String anonymous = null;
            public String session = null;
        }
    }

    @Data
    public static class JwtToken {
        public String secret = null;
    }
}
