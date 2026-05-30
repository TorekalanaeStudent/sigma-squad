package com.sigma_squad.computify.reservation.dto;

import jakarta.validation.constraints.NotNull;

/**
 * CreateReservationRequest - DTO for reservation creation request
 */
public record CreateReservationRequest(
    @NotNull(message = "Computer ID is required")
    Long computerId
) {}
