package com.acme.salarymanagement.service;

import com.acme.salarymanagement.model.User;
import com.acme.salarymanagement.model.UserRole;
import com.acme.salarymanagement.repository.UserRepository;
import java.time.Instant;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoUserInitializer {

    static final String DEMO_EMAIL = "demo@test.com";
    static final String DEMO_PASSWORD = "demo";

    @Bean
    @ConditionalOnProperty(prefix = "app.demo-user", name = "enabled", havingValue = "true")
    ApplicationRunner demoUserRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> userRepository.findByEmailIgnoreCase(DEMO_EMAIL)
                .orElseGet(() -> userRepository.save(User.seedUser(
                        DEMO_EMAIL,
                        passwordEncoder.encode(DEMO_PASSWORD),
                        UserRole.HR_MANAGER,
                        Instant.now())));
    }
}