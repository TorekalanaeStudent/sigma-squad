package com.sigma_squad.computify.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long extensionRequestId;

    @Column(nullable = true)
    private Long reservationId;

    @Column(nullable = false)
    private Long adminId;

    @Column(nullable = true)
    private String title;

    @Column(nullable = true, length = 500)
    private String message;

    @Column(nullable = true)
    private String type; // INFO, WARNING, ERROR, SUCCESS

    @Column(nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public void markAsRead() {
        this.isRead = true;
    }
}
