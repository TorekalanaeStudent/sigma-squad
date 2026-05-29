package com.sigma_squad.computify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.time.Instant;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long computerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(nullable = false)
    @Default
    private Instant reservedAt = Instant.now();

    @Column(nullable = false)
    private Instant expiresAt; // reservedAt + 5 minutes

    /**
     * Business rule: One ACTIVE reservation per user only
     */
    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    /**
     * Business rule: Check if reservation has expired
     */
    public boolean hasExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public enum ReservationStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED,
        CONFIRMED
    }
}
