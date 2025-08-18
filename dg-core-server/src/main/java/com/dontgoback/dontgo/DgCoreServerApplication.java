package com.dontgoback.dontgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DgCoreServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DgCoreServerApplication.class, args);
	}

}
