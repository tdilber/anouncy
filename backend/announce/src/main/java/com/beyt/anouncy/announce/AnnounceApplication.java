package com.beyt.anouncy.announce;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.announce"})
@ImportAutoConfiguration({GrpcClientAutoConfiguration.class})
public class AnnounceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnounceApplication.class, args);
	}

}
