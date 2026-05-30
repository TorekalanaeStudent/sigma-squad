package com.sigma_squad.computify.notification.dto;

import com.sigma_squad.computify.notification.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private Long extensionRequestId;
    private Long adminId;
    private boolean isRead;
    private Instant createdAt;

    public static NotificationDTO fromEntity(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .extensionRequestId(notification.getExtensionRequestId())
                .adminId(notification.getAdminId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
