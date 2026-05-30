package com.sigma_squad.computify.shared.websocket;

/**
 * NotificationType - Enum for notification categories
 */
public enum NotificationType {
    STUDENT_INFO,      // Info for students (session warnings, etc.)
    ADMIN_ALERT,       // Alerts for admins (session ended, user removed, etc.)
    SYSTEM             // System-wide announcements
}
