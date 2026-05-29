package com.sigma_squad.computify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

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
    @Default
    private Instant startTime = Instant.now();

    @Column
    private Instant endTime; // Nullable until session ends

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Default
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * Business rule: Session is active until ended
     */
    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

    /**
     * Business rule: End the session
     */
    public void endSession() {
        this.endTime = Instant.now();
        this.status = SessionStatus.ENDED;
    }

    public enum SessionStatus {
        ACTIVE,
        ENDED
    }
}
