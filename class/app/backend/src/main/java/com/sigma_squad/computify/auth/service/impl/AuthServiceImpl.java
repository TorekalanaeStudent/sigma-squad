package com.sigma_squad.computify.auth.service.impl;

import com.sigma_squad.computify.auth.service.IAuthService;
import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.shared.exception.UnauthorizedException;
import com.sigma_squad.computify.auth.service.IUserService;
import com.sigma_squad.computify.shared.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl - Implementation of IAuthService
 * Handles login and registration with JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User user = userService.createUser(
            request.name(),
            request.studentId(),
            request.email(),
            passwordEncoder.encode(request.password()),
            false
        );

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, userService.toDTO(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.getUserByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, userService.toDTO(user));
    }
}
