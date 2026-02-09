package com.yotor.global_logestics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		exclude = {UserDetailsServiceAutoConfiguration.class}
)
@Modulithic
@EnableScheduling
public class Application {

	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
