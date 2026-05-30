package com.sigma_squad.computify.shared.websocket;

import java.time.Instant;

/**
 * Notification - Data model for WebSocket notifications
 */
public record Notification(
    String title,
    String message,
    NotificationType type,
    Instant timestamp
) {
    public Notification(String title, String message, NotificationType type) {
        this(title, message, type, Instant.now());
    }
}
