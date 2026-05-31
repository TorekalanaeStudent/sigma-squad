package com.sigma_squad.computify.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * PasswordResetRequest - DTO for requesting password reset
 */
public record PasswordResetRequest(
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email
) {}
