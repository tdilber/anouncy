package com.beyt.anouncy.user;

import com.beyt.anouncy.user.config.AnouncyApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@ComponentScan(basePackages = "com.beyt.anouncy.common")
@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties({AnouncyApplicationProperties.class})
public class UserApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}
}


