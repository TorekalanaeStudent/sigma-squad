package com.sigma_squad.computify.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * VerifyEmailRequest - DTO for verifying email with code
 */
public record VerifyEmailRequest(
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Verification code is required")
    String code
) {}
