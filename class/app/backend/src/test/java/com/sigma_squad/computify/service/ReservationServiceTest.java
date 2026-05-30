package com.sigma_squad.computify.service;

import com.sigma_squad.computify.computer.service.IComputerService;
import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.repository.ReservationRepository;
import com.sigma_squad.computify.reservation.service.IReservationService;
import com.sigma_squad.computify.reservation.service.impl.ReservationServiceImpl;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import com.sigma_squad.computify.history.service.IAuditLogService;
import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.notification.service.INotificationService;
import com.sigma_squad.computify.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private IComputerService computerService;

    @Mock
    private IAuditLogService auditLogService;

    @Mock
    private ISessionService sessionService;

    @Mock
    private INotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = Reservation.builder()
                .id(1L)
                .userId(1L)
                .computerId(1L)
                .status(Reservation.ReservationStatus.ACTIVE)
                .reservedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
    }

    @Test
    void testCreateReservationSuccess() {
        // Given
        when(sessionService.existsActiveSessionByUserId(1L)).thenReturn(false);
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new com.sigma_squad.computify.auth.entity.User() {{ setName("Test User"); }}));
        when(notificationService.createReservationNotification(anyLong(), any(), any(), any())).thenReturn(null);

        // When
        Reservation result = reservationService.createReservation(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getComputerId());
        assertEquals(Reservation.ReservationStatus.ACTIVE, result.getStatus());
        verify(computerService, times(1)).markAsReserved(1L, 1L);
    }

    @Test
    void testCreateReservationUserAlreadyHasActive() {
        // Given
        when(sessionService.existsActiveSessionByUserId(1L)).thenReturn(false);
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            reservationService.createReservation(1L, 1L)
        );
    }

    @Test
    void testCreateReservationComputerNotAvailable() {
        // Given
        when(sessionService.existsActiveSessionByUserId(1L)).thenReturn(false);
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(false);

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            reservationService.createReservation(1L, 1L)
        );
    }

    @Test
    void testCancelReservationSuccess() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        reservationService.cancelReservation(1L);

        // Then
        assertEquals(Reservation.ReservationStatus.CANCELLED, testReservation.getStatus());
        verify(computerService, times(1)).markAsAvailable(1L);
    }

    @Test
    void testGetReservationByIdNotFound() {
        // Given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            reservationService.getReservationById(999L)
        );
    }
}
