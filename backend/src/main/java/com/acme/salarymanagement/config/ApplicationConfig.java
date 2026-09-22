package com.acme.salarymanagement.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CorsConfig.class, JwtProperties.class})
public class ApplicationConfig {
}
