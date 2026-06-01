package com.sigma_squad.computify.audit.service.impl;

import com.sigma_squad.computify.audit.dto.SessionAuditDTO;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.auth.repository.UserRepository;
import com.sigma_squad.computify.computer.entity.Computer;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditSessionServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AuditSessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ComputerRepository computerRepository;

    @InjectMocks
    private AuditSessionServiceImpl auditSessionService;

    private Session testSession;
    private User testUser;
    private Computer testComputer;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .studentId("2025-12345")
                .email("test@students.nu-laguna.edu.ph")
                .build();

        testComputer = Computer.builder()
                .id(1L)
                .computerNumber(1)
                .build();

        testSession = Session.builder()
                .id(1L)
                .userId(1L)
                .computerId(1L)
                .startTime(Instant.now().minusSeconds(3600))
                .endTime(Instant.now())
                .status(Session.SessionStatus.ENDED)
                .build();
    }

    @Test
    void testGetAllSessionsSuccess() {
        List<Session> sessions = List.of(testSession);
        when(sessionRepository.findAll()).thenReturn(sessions);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));

        List<SessionAuditDTO> result = auditSessionService.getAllSessions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSessionsEmpty() {
        when(sessionRepository.findAll()).thenReturn(List.of());

        List<SessionAuditDTO> result = auditSessionService.getAllSessions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSessionsWithMultipleSessions() {
        Session session2 = Session.builder()
                .id(2L)
                .userId(2L)
                .computerId(2L)
                .startTime(Instant.now().minusSeconds(7200))
                .endTime(Instant.now().minusSeconds(3600))
                .status(Session.SessionStatus.ENDED)
                .build();

        List<Session> sessions = List.of(testSession, session2);
        when(sessionRepository.findAll()).thenReturn(sessions);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(computerRepository.findById(anyLong())).thenReturn(Optional.of(testComputer));

        List<SessionAuditDTO> result = auditSessionService.getAllSessions();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSessionsWithMissingUserAndComputer() {
        when(sessionRepository.findAll()).thenReturn(List.of(testSession));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(computerRepository.findById(1L)).thenReturn(Optional.empty());

        List<SessionAuditDTO> result = auditSessionService.getAllSessions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sessionRepository, times(1)).findAll();
    }
}

