package com.sigma_squad.computify.session.dto;

import com.sigma_squad.computify.session.entity.Session;
import java.time.Instant;

/**
 * SessionDTO - DTO for session data transfer with time remaining calculation
 */
public record SessionDTO(
    Long id,
    Long userId,
    Long computerId,
    Instant startTime,
    Instant endTime,
    String status,
    Long minutesRemaining
) {
    public static SessionDTO fromEntity(Session session) {
        return new SessionDTO(
            session.getId(),
            session.getUserId(),
            session.getComputerId(),
            session.getStartTime(),
            session.getEndTime(),
            session.getStatus().toString(),
            session.getMinutesRemaining()
        );
    }
}
