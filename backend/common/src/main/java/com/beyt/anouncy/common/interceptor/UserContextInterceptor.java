package com.beyt.anouncy.common.interceptor;

import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.exception.ClientAuthorizationException;
import com.beyt.anouncy.common.model.UserJwtModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Autowired
    private UserContext userContext;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = request.getHeader("REQUEST-ID");
        String userId = request.getHeader("USER-ID");
        String anonymousUserId = request.getHeader("ANONYMOUS-USER-ID");
        String jwtModel = request.getHeader("JWT-MODEL");

        if (Strings.isNotBlank(userId) || Strings.isNotBlank(anonymousUserId) || Strings.isNotBlank(jwtModel) || Strings.isNotBlank(requestId)) {
            try {
                userContext.setRequestId(requestId);
                userContext.setUserId(UUID.fromString(userId));
                userContext.setAnonymousUserId(UUID.fromString(anonymousUserId));
                UserJwtModel userJwtModel = objectMapper.readValue(new String(Base64.getDecoder().decode(jwtModel), StandardCharsets.UTF_8), UserJwtModel.class);
                userContext.setUserJwtModel(userJwtModel);
            } catch (Exception e) {
                throw new ClientAuthorizationException("need.reauthorization", e);
            }
        }

        // (Double) Post Check. We want to all context full be loaded!
        if ((Objects.nonNull(userContext.getUserId()) || Objects.nonNull(userContext.getAnonymousUserId()) || Objects.nonNull(userContext.getUserJwtModel()) || Objects.nonNull(userContext.getRequestId()))
                && (Objects.isNull(userContext.getUserId()) || Objects.isNull(userContext.getAnonymousUserId()) || Objects.isNull(userContext.getUserJwtModel()) || Objects.isNull(userContext.getRequestId()))) {
            throw new ClientAuthorizationException("need.reauthorization", new Exception());
        }

        return true;
    }
}
