package com.beyt.anouncy.listing;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.listing"})
@ImportAutoConfiguration({GrpcClientAutoConfiguration.class})
public class AnnounceListingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnounceListingApplication.class, args);
	}

}
