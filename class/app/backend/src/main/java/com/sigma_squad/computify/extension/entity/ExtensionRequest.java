package com.sigma_squad.computify.extension.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "extension_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtensionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExtensionStatus status = ExtensionStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private Instant requestedAt = Instant.now();

    @Column
    private Instant respondedAt;

    @Column
    @Builder.Default
    private Instant expiresAt = Instant.now().plusSeconds(600); // 10 minutes

    public static final long EXTENSION_DURATION_MINUTES = 60;
    public static final long EXPIRY_DURATION_SECONDS = 600; // 10 minutes

    public boolean isPending() {
        return status == ExtensionStatus.PENDING;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt) && isPending();
    }

    public enum ExtensionStatus {
        PENDING,
        APPROVED,
        REJECTED,
        EXPIRED
    }
}
