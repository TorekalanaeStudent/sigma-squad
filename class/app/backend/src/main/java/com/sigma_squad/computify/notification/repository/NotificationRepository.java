package com.sigma_squad.computify.notification.repository;

import com.sigma_squad.computify.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAdminIdAndIsReadFalse(Long adminId);
    List<Notification> findByAdminId(Long adminId);
    List<Notification> findByExtensionRequestId(Long extensionRequestId);
}
