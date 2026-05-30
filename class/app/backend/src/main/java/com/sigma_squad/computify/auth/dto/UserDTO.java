package com.sigma_squad.computify.auth.dto;

import java.time.Instant;

/**
 * UserDTO - DTO for user data transfer
 */
public record UserDTO(
    Long id,
    String name,
    String studentId,
    String email,
    Boolean isAdmin,
    Instant createdAt
) {}
