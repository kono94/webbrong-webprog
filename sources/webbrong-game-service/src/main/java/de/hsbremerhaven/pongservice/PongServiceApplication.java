package de.hsbremerhaven.pongservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PongServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PongServiceApplication.class, args);
	}
}
