package com.sigma_squad.computify.reservation.dto;

import com.sigma_squad.computify.reservation.entity.Reservation;
import java.time.Instant;

/**
 * ReservationDTO - DTO for reservation data transfer
 */
public record ReservationDTO(
    Long id,
    Long userId,
    Long computerId,
    String status,
    Instant reservedAt,
    Instant expiresAt
) {
    public static ReservationDTO fromEntity(Reservation reservation) {
        return new ReservationDTO(
            reservation.getId(),
            reservation.getUserId(),
            reservation.getComputerId(),
            reservation.getStatus().toString(),
            reservation.getReservedAt(),
            reservation.getExpiresAt()
        );
    }
}
