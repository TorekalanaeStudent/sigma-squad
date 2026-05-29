package com.sigma_squad.computify.service;

import com.sigma_squad.computify.dto.AuthResponse;
import com.sigma_squad.computify.dto.LoginRequest;
import com.sigma_squad.computify.dto.RegisterRequest;
import com.sigma_squad.computify.entity.User;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.UnauthorizedException;
import com.sigma_squad.computify.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService - Answers: "What are the authentication rules?"
 * Handles login and registration with JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * Business rule: Only STUDENT registrations allowed (not LIBRARIAN)
     * Business rule: Email must end in @nu.edu.ph
     */
    public AuthResponse register(RegisterRequest request) {
        User user = userService.createUser(
            request.getName(),
            request.getStudentId(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            false // Always false for registration (students only)
        );

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, userService.toDTO(user));
    }

    /**
     * Login user
     * Business rule: Validate email and password
     */
    public AuthResponse login(LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, userService.toDTO(user));
    }
}
