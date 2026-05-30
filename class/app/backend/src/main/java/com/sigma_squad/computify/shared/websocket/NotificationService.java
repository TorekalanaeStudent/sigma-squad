package com.sigma_squad.computify.shared.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * NotificationService - Handles WebSocket notifications to clients
 * Sends real-time notifications to students and admins
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to a specific student
     * @param userId Student user ID
     * @param title Notification title
     * @param message Notification message
     */
    public void notifyStudent(Long userId, String title, String message) {
        String destination = "/user/" + userId + "/notifications";
        Notification notification = new Notification(title, message, NotificationType.STUDENT_INFO);
        messagingTemplate.convertAndSend(destination, notification);
    }

    /**
     * Send notification to all admins
     * @param title Notification title
     * @param message Notification message
     */
    public void notifyAdmins(String title, String message) {
        String destination = "/topic/admin-notifications";
        Notification notification = new Notification(title, message, NotificationType.ADMIN_ALERT);
        messagingTemplate.convertAndSend(destination, notification);
    }

    /**
     * Send system-wide notification
     * @param title Notification title
     * @param message Notification message
     */
    public void notifyAll(String title, String message) {
        String destination = "/topic/system-notifications";
        Notification notification = new Notification(title, message, NotificationType.SYSTEM);
        messagingTemplate.convertAndSend(destination, notification);
    }
}
