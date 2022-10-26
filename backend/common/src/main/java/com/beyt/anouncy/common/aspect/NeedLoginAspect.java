package com.beyt.anouncy.common.aspect;

import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.exception.ClientAuthorizationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Aspect
@Component
public class NeedLoginAspect {

    @Autowired
    private UserContext userContext;

    @Around("@annotation(NeedLogin)")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        if (Objects.isNull(userContext.getUserId())) {
            throw new ClientAuthorizationException("unauthorized");
        }

        return joinPoint.proceed();
    }
}
