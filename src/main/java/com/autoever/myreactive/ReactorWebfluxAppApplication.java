package com.autoever.myreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReactorWebfluxAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactorWebfluxAppApplication.class, args);
	}

	@Bean
	public WebProperties.Resources resources() {
		return new WebProperties.Resources();
	}
}
