package com.beyt.anouncy.user.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class UserInterceptorAppConfig implements WebMvcConfigurer {
   @Autowired
   private UserContextInterceptor userContextInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(userContextInterceptor);
   }
}
