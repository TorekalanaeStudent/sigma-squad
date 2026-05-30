package com.sigma_squad.computify.history.dto;

import java.time.Instant;

public record AuditLogDTO(
    Long id,
    Long userId,
    Long reservationId,
    String action,
    Instant timestamp,
    String details
) {
    public static AuditLogDTO fromEntity(com.sigma_squad.computify.history.entity.AuditLog entity) {
        return new AuditLogDTO(
            entity.getId(),
            entity.getUserId(),
            entity.getReservationId(),
            entity.getAction(),
            entity.getTimestamp(),
            entity.getDetails()
        );
    }
}
