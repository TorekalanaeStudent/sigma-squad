package com.sigma_squad.computify.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * RegisterRequest - DTO for user registration
 */
public record RegisterRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Student ID is required")
    String studentId,
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {}
