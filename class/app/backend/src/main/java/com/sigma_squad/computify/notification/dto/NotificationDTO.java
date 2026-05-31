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
    private Long reservationId;
    private Long adminId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private Instant createdAt;

    public static NotificationDTO fromEntity(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .extensionRequestId(notification.getExtensionRequestId())
                .reservationId(notification.getReservationId())
                .adminId(notification.getAdminId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
