package com.sigma_squad.computify.scheduler;

import com.sigma_squad.computify.reservation.entity.Reservation;
import com.sigma_squad.computify.reservation.entity.Reservation.ReservationStatus;
import com.sigma_squad.computify.reservation.service.IReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationSchedulerTest {

    @Mock
    private IReservationService reservationService;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @Test
    void testExpireReservationsNoExpired() {
        when(reservationService.getExpiredReservations())
            .thenReturn(Collections.emptyList());

        reservationScheduler.expireReservations();

        verify(reservationService, times(1)).getExpiredReservations();
        verify(reservationService, never()).expireReservation(any());
    }

    @Test
    void testExpireReservationsSingleExpired() {
        Reservation reservation = Reservation.builder()
            .id(1L)
            .userId(10L)
            .computerId(1L)
            .status(ReservationStatus.ACTIVE)
            .reservedAt(Instant.now().minusSeconds(300))
            .expiresAt(Instant.now().minusSeconds(60))
            .build();

        when(reservationService.getExpiredReservations())
            .thenReturn(Arrays.asList(reservation));

        reservationScheduler.expireReservations();

        verify(reservationService, times(1)).getExpiredReservations();
        verify(reservationService, times(1)).expireReservation(1L);
    }

    @Test
    void testExpireReservationsMultipleExpired() {
        Reservation res1 = Reservation.builder()
            .id(1L)
            .userId(10L)
            .computerId(1L)
            .status(ReservationStatus.ACTIVE)
            .expiresAt(Instant.now().minusSeconds(300))
            .build();

        Reservation res2 = Reservation.builder()
            .id(2L)
            .userId(11L)
            .computerId(2L)
            .status(ReservationStatus.ACTIVE)
            .expiresAt(Instant.now().minusSeconds(600))
            .build();

        when(reservationService.getExpiredReservations())
            .thenReturn(Arrays.asList(res1, res2));

        reservationScheduler.expireReservations();

        verify(reservationService, times(1)).getExpiredReservations();
        verify(reservationService, times(1)).expireReservation(1L);
        verify(reservationService, times(1)).expireReservation(2L);
    }
}
