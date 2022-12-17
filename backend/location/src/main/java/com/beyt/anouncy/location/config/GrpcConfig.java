package com.beyt.anouncy.location.config;

import com.beyt.anouncy.common.persist.AnnouncePersistServiceGrpc;
import com.beyt.anouncy.common.persist.AnonymousUserPersistServiceGrpc;
import com.beyt.anouncy.common.persist.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.persist.VotePersistServiceGrpc;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class GrpcConfig {
    //    @EnableGrpcSwagger(items = @GrpcClientItem(name = "persist-grpc-server", clazz = VotePersistServiceGrpc.VotePersistServiceBlockingStub.class))
    private Map<Class<?>, DynamicType.Loaded<Serializable>> converteds = new ConcurrentHashMap<>();
    private List<Class<?>> converteeList = new ArrayList<>();

    @Bean
    public Object createControllerVote() {
        return getRestController(VotePersistServiceGrpc.VotePersistServiceBlockingStub.class, "persist-grpc-server", "vote");
    }

    @Bean
    public Object createControllerRegion() {
        return getRestController(RegionPersistServiceGrpc.RegionPersistServiceBlockingStub.class, "persist-grpc-server", "region");
    }

    @Bean
    public Object createControllerAnonymousUser() {
        return getRestController(AnonymousUserPersistServiceGrpc.AnonymousUserPersistServiceBlockingStub.class, "persist-grpc-server", "anonymous-user");
    }

    @Bean
    public Object createControllerAnnounce() {
        return getRestController(AnnouncePersistServiceGrpc.AnnouncePersistServiceBlockingStub.class, "persist-grpc-server", "announce");
    }


    @GrpcClient("persist-grpc-server")
    private VotePersistServiceGrpc.VotePersistServiceBlockingStub votePersistServiceBlockingStub;

    @SneakyThrows
    private Object getRestController(Class<?> clazz, String clientName, String prefix) {
        List<Method> overridableMethods = getOverridableMethods(clazz);
        Set<Class<?>> convertees = new HashSet<>();

        for (Method overridableMethod : overridableMethods) {
            convertees.add(overridableMethod.getParameterTypes()[0]);
            convertees.add(overridableMethod.getReturnType());
        }
        converteeList.addAll(convertees);

        int index = 0;
        boolean isChanged = false;
        while (converteeList.size() > 0) {
            if (converteeList.size() <= index) {
//                if (!isChanged) {
//                    throw new IllegalStateException();
//                }
                index = 0;
                isChanged = false;
            }

            Class<?> convertee = converteeList.get(index);

            DynamicType.Loaded<Serializable> converted = convertPtoToDto(convertee);
            if (Objects.isNull(converted)) {
                index++;
                continue;
            } else {
                converteds.put(convertee, converted);
                converteeList.remove(convertee);
                isChanged = true;
            }
        }

        Thread.sleep(5000);

        List<GenericInterceptor> interceptors = new ArrayList<>();
        DynamicType.Builder<Object> objectBuilder = new ByteBuddy()
                .subclass(Object.class)
                .name(getClass().getPackageName() + "." + clazz.getSimpleName() + "DynamicController")
                .annotateType(AnnotationDescription.Builder
                        .ofType(RestController.class)
                        .build())
                .defineProperty("client", clazz, true)
                .annotateField(AnnotationDescription.Builder
                        .ofType(GrpcClient.class)
                        .define("value", clientName)
                        .build());

        for (Method overridableMethod : overridableMethods) {
            Class<?>[] parameterTypes = overridableMethod.getParameterTypes();
            Class<? extends Message> parameter = (Class<? extends Message>) parameterTypes[0];
            Class<? extends Message> returnType = (Class<? extends Message>) overridableMethod.getReturnType();
            GenericInterceptor interceptor = new GenericInterceptor(clientName, clazz, returnType, parameter, overridableMethod);
            interceptors.add(interceptor);
            objectBuilder = objectBuilder
                    .defineMethod(overridableMethod.getName(), converteds.get(returnType).getLoaded(), Modifier.PUBLIC)
                    .withParameter(converteds.get(parameter).getLoaded(), "param")
                    .annotateParameter(AnnotationDescription.Builder
                            .ofType(RequestBody.class)
                            .build())
                    .intercept(MethodDelegation.to(interceptor))
                    .annotateMethod(AnnotationDescription.Builder
                            .ofType(PostMapping.class)
                            .defineArray("value", "/" + clientName + "/" + prefix + "/" + overridableMethod.getName())
                            .build());
        }
        Class<?> controllerType = objectBuilder.make()
                .include(converteds.values().stream().toList())
                .load(getClass().getClassLoader())
                .getLoaded();

        interceptors.forEach(i -> i.setControllerType(controllerType));

        return controllerType
                .newInstance();
    }


    private List<Method> getOverridableMethods(Class<?> clazz) {
        return Stream.of(clazz.getMethods()).filter(c -> !Modifier.isFinal(c.getModifiers()) && !Modifier.isNative(c.getModifiers()) && !Modifier.isStatic(c.getModifiers())).filter(m -> !m.getDeclaringClass().equals(Object.class)).filter(m -> m.getParameterTypes().length == 1).toList();
    }

    private DynamicType.Loaded<Serializable> convertPtoToDto(Class<?> ptoClass) {

        DynamicType.Builder<Serializable> builder = new ByteBuddy().subclass(Serializable.class)
                .name(getClass().getPackageName() + "." + ptoClass.getSimpleName() + "DynamicDTO");

        var fieldList = Stream.of(ptoClass.getDeclaredFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).filter(f -> !f.getName().equalsIgnoreCase("unknownFields") && !f.getName().equalsIgnoreCase("memoizedIsInitialized")).toList();

        boolean isReturnNull = false;
        for (Field field : fieldList) {
            Class<?> type = field.getType();
            if (GeneratedMessageV3.class.isAssignableFrom(type)) {
                if (converteds.containsKey(type)) {
                    builder = builder.defineProperty(field.getName().substring(0, field.getName().length() - 1), converteds.get(type).getLoaded());
                } else if (ptoClass.isAssignableFrom(type)) {
                    continue;
                } else {
                    if (!converteeList.contains(type)) {
                        converteeList.add(type);
                    }
                    isReturnNull = true;
                }
            } else {
                builder = builder.defineProperty(field.getName().substring(0, field.getName().length() - 1), type);
            }
        }

        if (isReturnNull) {
            return null;
        }

        return builder
                .make().load(getClass().getClassLoader());
    }

    public static class GenericInterceptor {

        private Object grpcClient;
        private String grpcClientName;
        private Class<?> clazz;
        private Class<?> controllerType;
        private Class<?> returnTypePTO;
        private Class<? extends Message> paramTypePTO;
        private Method method;
        private ObjectMapper converter;

        public GenericInterceptor(String grpcClientName, Class<?> clazz, Class<? extends Message> returnTypePTO, Class<? extends Message> paramTypePTO, Method method) {
            this.grpcClientName = grpcClientName;
            this.clazz = clazz;
            this.returnTypePTO = returnTypePTO;
            this.paramTypePTO = paramTypePTO;
            this.method = method;
            converter = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            converter.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        }

        public void setControllerType(Class<?> controllerType) {
            this.controllerType = controllerType;
        }

        @RuntimeType
        @SneakyThrows
        public Object intercept(@AllArguments Object[] allArguments,
                                @This Object thiz,
                                @Origin Method method) {
            Object argument = allArguments[0];
            String s = "TEST " + argument.getClass().getName() + " " + argument;
            Class<?> returnTypeDTO = method.getReturnType();
            SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
                    .serializeAllExcept("unknownFields", "memoizedIsInitialized");
            FilterProvider filters = new SimpleFilterProvider()
                    .addFilter("myFilter", theFilter);

            String dtoStr = converter.writer(filters).writeValueAsString(allArguments[0]);
            Message.Builder newBuilder = (Message.Builder) paramTypePTO.getDeclaredMethod("newBuilder").invoke(null);
            Object param = fromJson(newBuilder, dtoStr, paramTypePTO);


            Object client = controllerType.getMethod("getClient").invoke(thiz);

            Message resultPto = (Message) clazz.getMethod(method.getName(), paramTypePTO).invoke(client, param);

            String resultPtoStr = toJson(resultPto);
            Object resultDto = converter.readValue(resultPtoStr, returnTypeDTO);

            log.info(resultDto.toString());
            return resultDto;
        }

        public static <T extends Message> Message fromJson(Message.Builder newBuilder, String json, Class<T> messageType) throws IOException {
            JsonFormat.parser().ignoringUnknownFields().merge(json, newBuilder);
            return (T) newBuilder.build();
        }

        public static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
            return JsonFormat.printer().print(messageOrBuilder);
        }

        public void setGrpcClient(Object grpcClient) {
            this.grpcClient = grpcClient;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }
}
