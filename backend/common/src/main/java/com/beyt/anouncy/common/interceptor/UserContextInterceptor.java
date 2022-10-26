package com.beyt.anouncy.common.interceptor;

import com.beyt.anouncy.common.context.UserContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Autowired
    private UserContext userContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("USER-ID");
        String anonymousUserId = request.getHeader("ANONYMOUS-USER-ID");

        if (Strings.isNotBlank(userId)) {
            userContext.setUserId(UUID.fromString(userId));
        }

        if (Strings.isNotBlank(anonymousUserId)) {
            userContext.setAnonymousUserId(UUID.fromString(anonymousUserId));
        }

        return true;
    }
}
