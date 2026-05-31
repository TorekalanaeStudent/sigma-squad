package com.sigma_squad.computify.auth.controller;

import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.PasswordResetRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;
import com.sigma_squad.computify.auth.dto.ResetPasswordRequest;
import com.sigma_squad.computify.auth.dto.VerifyEmailRequest;
import com.sigma_squad.computify.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequest request) {
        String message = authService.verifyEmail(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) {
        String message = authService.requestPasswordReset(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String message = authService.resetPassword(request);
        return ResponseEntity.ok(message);
    }
}
