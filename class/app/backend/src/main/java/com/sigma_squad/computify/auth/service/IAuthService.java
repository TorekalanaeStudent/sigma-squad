package com.sigma_squad.computify.auth.service;

import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.PasswordResetRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;
import com.sigma_squad.computify.auth.dto.ResetPasswordRequest;
import com.sigma_squad.computify.auth.dto.VerifyEmailRequest;

/**
 * IAuthService - Contract for authentication operations
 */
public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String requestPasswordReset(PasswordResetRequest request);
    String resetPassword(ResetPasswordRequest request);
    String verifyEmail(VerifyEmailRequest request);
}
