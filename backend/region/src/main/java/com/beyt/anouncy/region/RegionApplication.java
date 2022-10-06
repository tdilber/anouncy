package com.beyt.anouncy.region;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RegionApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegionApplication.class, args);
	}
}

@Data
class Region {

}
