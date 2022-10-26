package com.beyt.anouncy.listing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.listing"})
public class AnnounceListingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnounceListingApplication.class, args);
	}

}
