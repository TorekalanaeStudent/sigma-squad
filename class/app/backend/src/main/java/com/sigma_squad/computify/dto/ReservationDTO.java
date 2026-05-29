package com.sigma_squad.computify.dto;

import com.sigma_squad.computify.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;
    private Long userId;
    private Long computerId;
    private String status;
    private Instant reservedAt;
    private Instant expiresAt;

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
