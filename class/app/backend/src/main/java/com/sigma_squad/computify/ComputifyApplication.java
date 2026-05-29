package com.sigma_squad.computify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ComputifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComputifyApplication.class, args);
	}

}
