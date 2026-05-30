package com.sigma_squad.computify.reservation.service.impl;

import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.repository.ReservationRepository;
import com.sigma_squad.computify.computer.service.IComputerService;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import com.sigma_squad.computify.history.service.IAuditLogService;
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

/**
 * Unit tests for ReservationServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private IComputerService computerService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private IAuditLogService auditLogService;

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
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(false);
        when(computerService.isAvailable(1L)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.createReservation(1L, 1L);

        assertNotNull(result);
        assertEquals(Reservation.ReservationStatus.ACTIVE, result.getStatus());
        verify(computerService, times(1)).markAsReserved(1L, 1L);
    }

    @Test
    void testCreateReservationUserAlreadyHasActive() {
        when(reservationRepository.existsByUserIdAndStatus(1L, Reservation.ReservationStatus.ACTIVE)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> reservationService.createReservation(1L, 1L));
    }

    @Test
    void testCreateReservationComputerNotAvailable() {
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
}
