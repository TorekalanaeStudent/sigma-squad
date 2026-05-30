package com.sigma_squad.computify.notification.controller;

import com.sigma_squad.computify.notification.dto.NotificationDTO;
import com.sigma_squad.computify.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping("/admin/{adminId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Long adminId) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsForAdmin(adminId)
                .stream()
                .map(notificationService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/admin/{adminId}/all")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@PathVariable Long adminId) {
        List<NotificationDTO> notifications = notificationService.getAllNotificationsForAdmin(adminId)
                .stream()
                .map(notificationService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/admin/{adminId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long adminId) {
        int count = notificationService.getUnreadCountForAdmin(adminId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markMultipleAsRead(notificationIds);
        return ResponseEntity.ok().build();
    }
}
