package com.sigma_squad.computify.reservation.service.impl;

import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.repository.ReservationRepository;
import com.sigma_squad.computify.computer.service.IComputerService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private IComputerService computerService;

    @Mock
    private ISessionService sessionService;

    @Mock
    private IAuditLogService auditLogService;

    @Mock
    private INotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUserId(1L);
        testReservation.setComputerId(1L);
        testReservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        testReservation.setReservedAt(Instant.now());
        testReservation.setExpiresAt(Instant.now().plusSeconds(300));
    }

    @Test
    void testCreateReservationSuccess() {
        when(sessionService.existsActiveSessionByUserId(1L)).thenReturn(false);
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new com.sigma_squad.computify.auth.entity.User() {{ setName("Test User"); }}));
        when(notificationService.createReservationNotification(anyLong(), any(), any(), any())).thenReturn(null);

        Reservation result = reservationService.createReservation(1L, 1L);

        assertNotNull(result);
        assertEquals(Reservation.ReservationStatus.ACTIVE, result.getStatus());
        verify(computerService, times(1)).markAsReserved(1L, 1L);
    }

    @Test
    void testCreateReservationUserAlreadyHasActive() {
        when(sessionService.existsActiveSessionByUserId(1L)).thenReturn(false);
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> reservationService.createReservation(1L, 1L));
    }

    @Test
    void testCreateReservationComputerNotAvailable() {
        when(sessionService.existsActiveSessionByUserId(1L)).thenReturn(false);
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> reservationService.createReservation(1L, 1L));
    }

    @Test
    void testGetReservationByIdSuccess() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        Reservation result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetReservationByIdNotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.getReservationById(1L));
    }

    @Test
    void testCancelReservationSuccess() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.cancelReservation(1L);

        assertEquals(Reservation.ReservationStatus.CANCELLED, testReservation.getStatus());
        verify(computerService, times(1)).markAsAvailable(1L);
    }

    @Test
    void testConfirmReservationSuccess() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.confirmReservation(1L);

        assertEquals(Reservation.ReservationStatus.CONFIRMED, testReservation.getStatus());
        verify(computerService, times(1)).markAsInUse(1L, 1L);
    }

    @Test
    void testGetReservationHistorySuccess() {
        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(2L);
        confirmedReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);

        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setId(3L);
        cancelledReservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        Reservation activeReservation = new Reservation();
        activeReservation.setId(4L);
        activeReservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        List<Reservation> allReservations = List.of(
            confirmedReservation,
            cancelledReservation,
            activeReservation
        );

        when(reservationRepository.findAll()).thenReturn(allReservations);

        List<Reservation> history = reservationService.getReservationHistory();

        assertNotNull(history);
        assertEquals(2, history.size());
        assertTrue(history.stream().allMatch(r -> !r.getStatus().equals(Reservation.ReservationStatus.ACTIVE)));
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testGetReservationHistoryEmpty() {
        when(reservationRepository.findAll()).thenReturn(new ArrayList<>());

        List<Reservation> history = reservationService.getReservationHistory();

        assertNotNull(history);
        assertEquals(0, history.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testGetReservationHistoryExcludesActiveOnly() {
        Reservation activeReservation1 = new Reservation();
        activeReservation1.setId(1L);
        activeReservation1.setStatus(Reservation.ReservationStatus.ACTIVE);

        Reservation activeReservation2 = new Reservation();
        activeReservation2.setId(2L);
        activeReservation2.setStatus(Reservation.ReservationStatus.ACTIVE);

        List<Reservation> allReservations = List.of(activeReservation1, activeReservation2);

        when(reservationRepository.findAll()).thenReturn(allReservations);

        List<Reservation> history = reservationService.getReservationHistory();

        assertNotNull(history);
        assertEquals(0, history.size());
        verify(reservationRepository, times(1)).findAll();
    }
}
