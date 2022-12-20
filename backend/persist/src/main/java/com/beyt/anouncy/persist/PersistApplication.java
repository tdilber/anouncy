package com.beyt.anouncy.persist;

import com.beyt.anouncy.common.persist.v1.VotePersistServiceGrpc;
import com.beyt.anouncy.persist.swagger.annotation.EnableGrpcSwagger;
import com.beyt.anouncy.persist.swagger.annotation.GrpcClientItem;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGrpcSwagger(items = @GrpcClientItem(name = "persist-grpc-server", clazz = VotePersistServiceGrpc.VotePersistServiceBlockingStub.class))
@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class, // Create required server beans
        GrpcServerFactoryAutoConfiguration.class}) // Support @GrpcClient annotation
public class PersistApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistApplication.class, args);
    }

}
