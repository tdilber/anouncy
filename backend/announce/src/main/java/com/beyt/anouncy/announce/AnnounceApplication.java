package com.beyt.anouncy.announce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.announce"})
public class AnnounceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnounceApplication.class, args);
	}

}
