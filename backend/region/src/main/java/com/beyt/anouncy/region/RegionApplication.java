package com.beyt.anouncy.region;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.region"})
@ImportAutoConfiguration({GrpcClientAutoConfiguration.class, GrpcServerAutoConfiguration.class, GrpcServerFactoryAutoConfiguration.class})
public class RegionApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegionApplication.class, args);
	}
}
