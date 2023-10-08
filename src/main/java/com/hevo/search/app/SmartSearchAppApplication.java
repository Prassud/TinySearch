package com.hevo.search.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hevo.search.app.*" })
public class SmartSearchAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartSearchAppApplication.class, args);
	}

}
