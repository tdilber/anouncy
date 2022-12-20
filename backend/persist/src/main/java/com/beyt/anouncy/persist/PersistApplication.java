package com.beyt.anouncy.persist;

import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration({GrpcServerAutoConfiguration.class, GrpcServerFactoryAutoConfiguration.class})
public class PersistApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistApplication.class, args);
    }

}
