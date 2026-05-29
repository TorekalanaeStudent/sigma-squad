package com.sigma_squad.computify.dto;

import com.sigma_squad.computify.entity.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
    private Long id;
    private Long userId;
    private Long computerId;
    private Instant startTime;
    private Instant endTime;
    private String status;

    public static SessionDTO fromEntity(Session session) {
        return new SessionDTO(
            session.getId(),
            session.getUserId(),
            session.getComputerId(),
            session.getStartTime(),
            session.getEndTime(),
            session.getStatus().toString()
        );
    }
}
