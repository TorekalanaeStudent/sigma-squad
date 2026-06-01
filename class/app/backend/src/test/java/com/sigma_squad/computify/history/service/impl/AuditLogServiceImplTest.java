package com.sigma_squad.computify.history.service.impl;

import com.sigma_squad.computify.history.dto.AuditLogDTO;
import com.sigma_squad.computify.history.entity.AuditLog;
import com.sigma_squad.computify.history.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditLogServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog testAuditLog;

    @BeforeEach
    void setUp() {
        testAuditLog = AuditLog.builder()
                .id(1L)
                .userId(1L)
                .reservationId(1L)
                .action("RESERVATION_CREATED")
                .details("Reservation created for computer 1")
                .timestamp(Instant.now())
                .build();
    }

    @Test
    void testLogAuditEventSuccess() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        auditLogService.log(1L, 1L, "RESERVATION_CREATED", "Reservation created for computer 1");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testGetUserHistorySuccess() {
        List<AuditLog> logs = List.of(testAuditLog);
        when(auditLogRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(logs);

        List<AuditLogDTO> result = auditLogService.getUserHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository, times(1)).findByUserIdOrderByTimestampDesc(1L);
    }

    @Test
    void testGetUserHistoryEmpty() {
        when(auditLogRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of());

        List<AuditLogDTO> result = auditLogService.getUserHistory(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(auditLogRepository, times(1)).findByUserIdOrderByTimestampDesc(1L);
    }

    @Test
    void testGetReservationHistorySuccess() {
        List<AuditLog> logs = List.of(testAuditLog);
        when(auditLogRepository.findByReservationId(1L)).thenReturn(logs);

        List<AuditLogDTO> result = auditLogService.getReservationHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository, times(1)).findByReservationId(1L);
    }

    @Test
    void testGetReservationHistoryEmpty() {
        when(auditLogRepository.findByReservationId(1L)).thenReturn(List.of());

        List<AuditLogDTO> result = auditLogService.getReservationHistory(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(auditLogRepository, times(1)).findByReservationId(1L);
    }

    @Test
    void testLogMultipleAuditEvents() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        auditLogService.log(1L, 1L, "RESERVATION_CREATED", "Reservation created");
        auditLogService.log(1L, 1L, "SESSION_STARTED", "Session started");

        verify(auditLogRepository, times(2)).save(any(AuditLog.class));
    }

    @Test
    void testLogPreservesAllDetails() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        auditLogService.log(1L, 1L, "TEST_ACTION", "Test details");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}

