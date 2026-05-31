package com.sigma_squad.computify.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ResetPasswordRequest - DTO for resetting password with token
 */
public record ResetPasswordRequest(
    @NotBlank(message = "Token is required")
    String token,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String newPassword,
    
    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {}
