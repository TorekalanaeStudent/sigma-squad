package com.sigma_squad.computify.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - DTO for login credentials
 */
public record LoginRequest(
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {}
