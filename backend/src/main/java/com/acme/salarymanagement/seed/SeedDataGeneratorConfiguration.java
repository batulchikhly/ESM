package com.acme.salarymanagement.seed;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SeedProperties.class)
public class SeedDataGeneratorConfiguration {

    @Bean
    SeedDataGenerator seedDataGenerator() {
        return new SeedDataGenerator();
    }
}
