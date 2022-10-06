package com.beyt.anouncy.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "anouncy", ignoreUnknownFields = false)
public class AnouncyApplicationProperties {
    private final Password password = new Password();
    private final JwtToken jwtToken = new JwtToken();

    @Getter
    @Setter
    public static class Password {
        private final Salt salt = new Salt();

        @Getter
        @Setter
        public static class Salt {
            private String user = null;
            private String anonymous = null;
            private String session = null;
        }
    }

    @Getter
    @Setter
    public static class JwtToken {
        public String secret = null;
    }
}
