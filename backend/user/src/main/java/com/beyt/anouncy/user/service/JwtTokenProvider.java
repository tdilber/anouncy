package com.beyt.anouncy.user.service;

import com.beyt.anouncy.common.model.UserJwtModel;
import com.beyt.anouncy.user.config.AnouncyApplicationProperties;
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

    private static final String TOKEN_MODEL = "model";

    private final ConfigurationService configurationService;

    private final AnouncyApplicationProperties applicationProperties;

    private Key key;

    private JwtParser jwtParser;

    private final long tokenValidityOneYear;

    private final ObjectMapper objectMapper;

    public JwtTokenProvider(ConfigurationService configurationService, AnouncyApplicationProperties applicationProperties) {
        this.configurationService = configurationService;
        this.applicationProperties = applicationProperties;
        objectMapper = new ObjectMapper();
        this.tokenValidityOneYear = 31_556_926_000L;
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
    public String createToken(UserJwtModel userModel) {

        long now = (new Date()).getTime();

        String userModelStr = objectMapper.writeValueAsString(userModel);

        Date validity = new Date(now + this.tokenValidityOneYear);

        return Jwts
                .builder()
                .setSubject(userModel.getUsername())
                .claim(TOKEN_MODEL, userModelStr)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    @SneakyThrows
    public UserJwtModel getTokenModel(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        return objectMapper.readValue(claims.get(TOKEN_MODEL).toString(), UserJwtModel.class);
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
