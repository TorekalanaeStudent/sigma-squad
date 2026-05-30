package com.sigma_squad.computify.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AuthResponse - DTO for authentication response with JWT token
 */
public record AuthResponse(
    @NotBlank(message = "Token is required")
    String token,
    
    UserDTO user
) {}
