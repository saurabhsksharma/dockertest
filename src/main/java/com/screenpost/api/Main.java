package com.screenpost.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.screenpost.api","com.screenpost.api.controller",
"com.screenpost.api.service"})
@EnableAutoConfiguration
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
