package com.fruitwarehouse.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins("http://localhost:4000", "http://localhost:4002")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}
}