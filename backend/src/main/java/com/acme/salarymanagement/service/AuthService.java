package com.acme.salarymanagement.service;

import com.acme.salarymanagement.config.JwtService;
import com.acme.salarymanagement.dto.LoginRequest;
import com.acme.salarymanagement.dto.LoginResponse;
import com.acme.salarymanagement.exception.InvalidCredentialsException;
import com.acme.salarymanagement.model.User;
import com.acme.salarymanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .filter(User::isActive)
                .filter(candidate -> candidate.getRole() != null)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        JwtService.IssuedToken token = jwtService.issue(user.getEmail(), user.getRole().name());
        return new LoginResponse(token.value(), "Bearer", token.expiresAt(), user.getEmail(), user.getRole().name());
    }
}
