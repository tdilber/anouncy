package com.beyt.anouncy.region;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.region"})
public class RegionApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegionApplication.class, args);
	}
}
