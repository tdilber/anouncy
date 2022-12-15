package com.beyt.anouncy.location;

import com.beyt.doc.aggregator.annotation.EnableGroupedSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableGroupedSwagger
@SpringBootApplication(scanBasePackages = {"com.beyt.anouncy.common", "com.beyt.anouncy.location"})
public class LocationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocationApplication.class, args);
	}

}
