package com.sigma_squad.computify.notification.service;

import com.sigma_squad.computify.notification.dto.NotificationDTO;
import com.sigma_squad.computify.notification.entity.Notification;

import java.util.List;

public interface INotificationService {
    Notification createNotification(Long extensionRequestId, Long adminId);
    Notification getNotificationById(Long id);
    List<Notification> getUnreadNotificationsForAdmin(Long adminId);
    List<Notification> getAllNotificationsForAdmin(Long adminId);
    List<Notification> getNotificationsForExtensionRequest(Long extensionRequestId);
    void markAsRead(Long notificationId);
    void markMultipleAsRead(List<Long> notificationIds);
    int getUnreadCountForAdmin(Long adminId);
    NotificationDTO toDTO(Notification notification);
}
