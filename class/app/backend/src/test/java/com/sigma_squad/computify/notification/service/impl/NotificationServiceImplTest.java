package com.sigma_squad.computify.notification.service.impl;

import com.sigma_squad.computify.notification.dto.NotificationDTO;
import com.sigma_squad.computify.notification.entity.Notification;
import com.sigma_squad.computify.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .id(1L)
                .extensionRequestId(1L)
                .adminId(1L)
                .title("Test Notification")
                .message("This is a test notification")
                .type("INFO")
                .isRead(false)
                .build();
    }

    @Test
    void testCreateNotificationSuccess() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.createNotification(1L, 1L);

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateReservationNotificationSuccess() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.createReservationNotification(1L, "Reservation", "Test message", "INFO");

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetNotificationByIdSuccess() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        Notification result = notificationService.getNotificationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUnreadNotificationsForAdminSuccess() {
        List<Notification> notifications = List.of(testNotification);
        when(notificationRepository.findByAdminIdAndIsReadFalse(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getUnreadNotificationsForAdmin(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByAdminIdAndIsReadFalse(1L);
    }

    @Test
    void testGetUnreadNotificationsForAdminEmpty() {
        when(notificationRepository.findByAdminIdAndIsReadFalse(1L)).thenReturn(List.of());

        List<Notification> result = notificationService.getUnreadNotificationsForAdmin(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationRepository, times(1)).findByAdminIdAndIsReadFalse(1L);
    }

    @Test
    void testGetAllNotificationsForAdminSuccess() {
        List<Notification> notifications = List.of(testNotification);
        when(notificationRepository.findByAdminId(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getAllNotificationsForAdmin(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByAdminId(1L);
    }

    @Test
    void testGetNotificationsForExtensionRequestSuccess() {
        List<Notification> notifications = List.of(testNotification);
        when(notificationRepository.findByExtensionRequestId(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsForExtensionRequest(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByExtensionRequestId(1L);
    }

    @Test
    void testMarkAsReadSuccess() {
        testNotification.setRead(false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        notificationService.markAsRead(1L);

        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testMarkMultipleAsReadSuccess() {
        List<Notification> notifications = List.of(testNotification);
        List<Long> ids = List.of(1L);
        when(notificationRepository.findAllById(ids)).thenReturn(notifications);

        notificationService.markMultipleAsRead(ids);

        verify(notificationRepository, times(1)).findAllById(ids);
        verify(notificationRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void testGetUnreadCountForAdminSuccess() {
        List<Notification> notifications = List.of(testNotification, testNotification);
        when(notificationRepository.findByAdminIdAndIsReadFalse(1L)).thenReturn(notifications);

        int result = notificationService.getUnreadCountForAdmin(1L);

        assertEquals(2, result);
        verify(notificationRepository, times(1)).findByAdminIdAndIsReadFalse(1L);
    }

    @Test
    void testGetUnreadCountForAdminZero() {
        when(notificationRepository.findByAdminIdAndIsReadFalse(1L)).thenReturn(List.of());

        int result = notificationService.getUnreadCountForAdmin(1L);

        assertEquals(0, result);
        verify(notificationRepository, times(1)).findByAdminIdAndIsReadFalse(1L);
    }

    @Test
    void testToDTOSuccess() {
        NotificationDTO dto = notificationService.toDTO(testNotification);

        assertNotNull(dto);
    }
}

