package com.sigma_squad.computify.notification.service.impl;

import com.sigma_squad.computify.notification.dto.NotificationDTO;
import com.sigma_squad.computify.notification.entity.Notification;
import com.sigma_squad.computify.notification.repository.NotificationRepository;
import com.sigma_squad.computify.notification.service.INotificationService;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(Long extensionRequestId, Long adminId) {
        Notification notification = Notification.builder()
                .extensionRequestId(extensionRequestId)
                .adminId(adminId)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    @Override
    public List<Notification> getUnreadNotificationsForAdmin(Long adminId) {
        return notificationRepository.findByAdminIdAndIsReadFalse(adminId);
    }

    @Override
    public List<Notification> getAllNotificationsForAdmin(Long adminId) {
        return notificationRepository.findByAdminId(adminId);
    }

    @Override
    public List<Notification> getNotificationsForExtensionRequest(Long extensionRequestId) {
        return notificationRepository.findByExtensionRequestId(extensionRequestId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    public void markMultipleAsRead(List<Long> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
    }

    @Override
    public int getUnreadCountForAdmin(Long adminId) {
        return (int) notificationRepository.findByAdminIdAndIsReadFalse(adminId).stream().count();
    }

    @Override
    public NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.fromEntity(notification);
    }
}
