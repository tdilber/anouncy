package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.config.AnouncyApplicationProperties;
import com.beyt.anouncy.user.dto.UserJwtModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";

    private final ConfigurationService configurationService;

    private final AnouncyApplicationProperties applicationProperties;

    private Key key;

    private JwtParser jwtParser;

    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInMillisecondsForRememberMe;

    private final ObjectMapper objectMapper;

    public JwtTokenProvider(ConfigurationService configurationService, AnouncyApplicationProperties applicationProperties) {
        this.configurationService = configurationService;
        this.applicationProperties = applicationProperties;
        objectMapper = new ObjectMapper();
        this.tokenValidityInMilliseconds = 3_600_000; // One Hour
        this.tokenValidityInMillisecondsForRememberMe = 31_556_926_000L;
        init();
    }

    public synchronized void init() {
        byte[] keyBytes;
        String secret = configurationService.get("anouncy.jwtToken.secret") + applicationProperties.getJwtToken().getSecret();
        keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    @SneakyThrows
    public String createToken(UserJwtModel userModel, boolean rememberMe) {

        long now = (new Date()).getTime();

        String userModelStr = objectMapper.writeValueAsString(userModel);

        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts
                .builder()
                .setSubject(userModel.getUsername())
                .claim(AUTHORITIES_KEY, userModelStr)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    @SneakyThrows
    public UserJwtModel getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        return objectMapper.readValue(claims.get(AUTHORITIES_KEY).toString(), UserJwtModel.class);
    }

    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
