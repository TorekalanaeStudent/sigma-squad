package com.sigma_squad.computify.session.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long computerId;

    @Column(nullable = false)
    @Builder.Default
    private Instant startTime = Instant.now();

    @Column
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

    public void endSession() {
        this.endTime = Instant.now();
        this.status = SessionStatus.ENDED;
    }

    public long getMinutesRemaining() {
        if (endTime == null || !isActive()) {
            return 0;
        }
        long diff = endTime.toEpochMilli() - Instant.now().toEpochMilli();
        return diff / 60000;
    }

    public enum SessionStatus {
        ACTIVE,
        ENDED
    }
}
