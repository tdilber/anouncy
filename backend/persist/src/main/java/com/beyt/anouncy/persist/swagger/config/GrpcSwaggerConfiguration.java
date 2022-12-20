package com.beyt.anouncy.persist.swagger.config;

import com.beyt.anouncy.persist.swagger.annotation.EnableGrpcSwagger;
import com.beyt.anouncy.persist.swagger.annotation.GrpcClientItem;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import net.devh.boot.grpc.client.inject.GrpcClientBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.util.Pair;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class GrpcSwaggerConfiguration implements ImportAware {

    @Autowired(required = false)
    private GrpcChannelFactory grpcChannelFactory;
    @Autowired(required = false)
    private GrpcClientBeanPostProcessor postProcessor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    @Qualifier("controllerEndpointHandlerMapping")
//    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        if (Objects.isNull(handlerMapping) ||
                Objects.isNull(postProcessor) ||
                Objects.isNull(grpcChannelFactory)) {
            return;
        }

        var items = Stream.of(importMetadata.getAnnotations().get(EnableGrpcSwagger.class).asAnnotationAttributes().get("items")).flatMap(c -> Stream.of((GrpcClientItem[]) c)).map(c -> Pair.of(c.name(), c.clazz())).toList();

//        items.forEach(p -> {
//            Class<?> clazz = p.getSecond();
//            String clientName = p.getFirst();
//            Object grpcClient = getGrpcClient(clientName, clazz);
//
//            extracted(clazz, grpcClient, clientName);
//        });
    }
//
//    @SneakyThrows
//    private void extracted(Class<?> clazz, Object grpcClient, String clientName) {
//        Object restController = getRestController(clazz, grpcClient, clientName);
//        List<Method> overridableMethods = getOverridableMethods(restController.getClass());
//
//        Method registerHandlerMethod = AbstractHandlerMethodMapping.class.getDeclaredMethod("detectHandlerMethods", Object.class);
//        registerHandlerMethod.setAccessible(true);
//        registerHandlerMethod.invoke(handlerMapping, restController);
////        for (Method overridableMethod : overridableMethods) {
////            Class<?>[] parameterTypes = overridableMethod.getParameterTypes();
////            if (parameterTypes.length != 1) {
////                continue;
////            }
////
////            Object naber = overridableMethod.invoke(restController, new TestDTO("Naber"));
////
////            log.info("RESULT : {}", naber);
////
//////            Method registerHandlerMethod = handlerMapping.getClass().getDeclaredMethod("registerHandlerMethod", Object.class, Method.class, RequestMappingInfo.class);
//////            registerHandlerMethod.setAccessible(true);
//////            registerHandlerMethod.invoke(handlerMapping, restController, overridableMethod, RequestMappingInfo.paths("/" + clientName + "/" + overridableMethod.getName())
//////                    .methods(RequestMethod.POST)
//////                    .build());
////
//////            handlerMapping.registerMapping(
//////                    RequestMappingInfo
//////                            .paths("/" + clientName + "/" + overridableMethod.getName())
//////                            .methods(RequestMethod.POST)
//////                            .customCondition(new PatternsRequestCondition())
//////                            .build(),
//////                    restController,
//////                    overridableMethod);
////        }
//
//
//    }
//
//    @SneakyThrows
//    private <T> T getGrpcClient(String name, Class<T> clazz) {
//        Channel channel = grpcChannelFactory.createChannel(name);
//        Method method = postProcessor.getClass().getDeclaredMethod("valueForMember", String.class, Member.class, Class.class, Channel.class);
//
//        method.setAccessible(true);
//        T stub = (T) method.invoke(postProcessor, name, null, clazz, channel);
//        return stub;
//    }
//
//    private List<Method> getOverridableMethods(Class<?> clazz) {
//        return Stream.of(clazz.getMethods()).filter(c -> !Modifier.isFinal(c.getModifiers()) && !Modifier.isNative(c.getModifiers()) && !Modifier.isStatic(c.getModifiers())).filter(m -> !m.getDeclaringClass().equals(Object.class)).toList();
//    }
//

}
