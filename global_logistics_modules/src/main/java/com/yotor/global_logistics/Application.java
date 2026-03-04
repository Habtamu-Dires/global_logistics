package com.yotor.global_logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		exclude = {UserDetailsServiceAutoConfiguration.class}
)
@Modulithic
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties
public class Application {

	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
