package com.sigma_squad.computify.auth.service.impl;

import com.sigma_squad.computify.auth.service.IAuthService;
import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.PasswordResetRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;
import com.sigma_squad.computify.auth.dto.ResetPasswordRequest;
import com.sigma_squad.computify.auth.dto.VerifyEmailRequest;
import com.sigma_squad.computify.auth.entity.PasswordReset;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.auth.entity.VerificationToken;
import com.sigma_squad.computify.auth.repository.PasswordResetRepository;
import com.sigma_squad.computify.auth.repository.VerificationTokenRepository;
import com.sigma_squad.computify.shared.exception.UnauthorizedException;
import com.sigma_squad.computify.auth.service.IUserService;
import com.sigma_squad.computify.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.Random;

/**
 * AuthServiceImpl - Implementation of IAuthService
 * Handles login, registration, email verification, and password reset with JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetRepository passwordResetRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSender javaMailSender;

    @Value("${password-reset.token-expiration-hours:1}")
    private Long tokenExpirationHours;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User user = userService.createUser(
            request.name(),
            request.studentId(),
            request.email(),
            passwordEncoder.encode(request.password()),
            false
        );

        // Generate verification code
        String code = generateVerificationCode();
        Instant expiresAt = Instant.now().plusSeconds(24 * 3600); // 24 hours

        VerificationToken token = VerificationToken.builder()
            .code(code)
            .user(user)
            .expiresAt(expiresAt)
            .build();

        verificationTokenRepository.save(token);

        // Send verification email
        sendVerificationEmail(user.getEmail(), code);

        // Return response without JWT (user not verified yet)
        return new AuthResponse(null, userService.toDTO(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.getUserByEmail(request.email());

        if (!user.getIsVerified()) {
            throw new UnauthorizedException("Email not verified. Please check your email for verification code.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, userService.toDTO(user));
    }

    @Override
    public String requestPasswordReset(PasswordResetRequest request) {
        User user = userService.getUserByEmail(request.email());

        // Delete any existing reset tokens for this user
        passwordResetRepository.deleteByUserId(user.getId());

        // Generate new token
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(tokenExpirationHours * 3600);

        PasswordReset reset = PasswordReset.builder()
            .token(token)
            .user(user)
            .expiresAt(expiresAt)
            .build();

        passwordResetRepository.save(reset);

        // Send email with reset link
        sendPasswordResetEmail(user.getEmail(), token);

        return "Password reset link sent to your email";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new UnauthorizedException("Passwords do not match");
        }

        PasswordReset reset = passwordResetRepository.findByToken(request.token())
            .orElseThrow(() -> new UnauthorizedException("Invalid or expired reset token"));

        if (!reset.isValid()) {
            throw new UnauthorizedException("Password reset token has expired or already been used");
        }

        User user = reset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userService.saveUser(user);

        // Mark token as used
        reset.setUsed(true);
        passwordResetRepository.save(reset);

        return "Password reset successfully";
    }

    @Override
    public String verifyEmail(VerifyEmailRequest request) {
        User user = userService.getUserByEmail(request.email());

        VerificationToken token = verificationTokenRepository.findByCode(request.code())
            .orElseThrow(() -> new UnauthorizedException("Invalid verification code"));

        if (!token.isValid()) {
            throw new UnauthorizedException("Verification code has expired or already been used");
        }

        if (!token.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Verification code does not match this email");
        }

        // Mark user as verified
        user.setIsVerified(true);
        userService.saveUser(user);

        // Mark token as used
        token.setUsed(true);
        verificationTokenRepository.save(token);

        return "Email verified successfully";
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nc8stestemail@gmail.com");
            message.setTo(email);
            message.setSubject("Email Verification - CLASS System");
            message.setText(buildVerificationEmail(code));

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    private void sendPasswordResetEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nc8stestemail@gmail.com");
            message.setTo(email);
            message.setSubject("Password Reset - CLASS System");
            message.setText(buildPasswordResetEmail(token));

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    private String buildVerificationEmail(String code) {
        return "Hello,\n\n" +
                "Welcome to the CLASS system!\n\n" +
                "Your verification code is:\n" +
                code + "\n\n" +
                "This code will expire in 24 hours.\n\n" +
                "If you did not create this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "CLASS Administration Team";
    }

    private String buildPasswordResetEmail(String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        
        return "Hello,\n\n" +
                "You have requested to reset your password for the CLASS system.\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in " + tokenExpirationHours + " hour(s).\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "CLASS Administration Team";
    }
}
