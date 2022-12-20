package com.beyt.anouncy.persist.swagger.annotation;


import com.beyt.anouncy.persist.swagger.config.GrpcSwaggerSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(GrpcSwaggerSelector.class)
public @interface EnableGrpcSwagger {
    GrpcClientItem[] items();

    AdviceMode mode() default AdviceMode.PROXY;

}
