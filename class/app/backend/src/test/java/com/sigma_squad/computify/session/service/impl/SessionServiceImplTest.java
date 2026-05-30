package com.sigma_squad.computify.session.service.impl;

import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.repository.SessionRepository;
import com.sigma_squad.computify.computer.service.IComputerService;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SessionServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private IComputerService computerService;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Session testSession;

    @BeforeEach
    void setUp() {
        testSession = new Session();
        testSession.setId(1L);
        testSession.setUserId(1L);
        testSession.setComputerId(1L);
        testSession.setStatus(Session.SessionStatus.ACTIVE);
    }

    @Test
    void testStartSessionSuccess() {
        when(sessionRepository.findByUserIdAndStatus(1L, Session.SessionStatus.ACTIVE)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        Session result = sessionService.startSession(1L, 1L);

        assertNotNull(result);
        assertEquals(Session.SessionStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testStartSessionUserAlreadyHasActive() {
        when(sessionRepository.findByUserIdAndStatus(1L, Session.SessionStatus.ACTIVE)).thenReturn(Optional.of(testSession));

        assertThrows(BusinessRuleException.class, () -> sessionService.startSession(1L, 1L));
    }

    @Test
    void testGetSessionByIdSuccess() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        Session result = sessionService.getSessionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetSessionByIdNotFound() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sessionService.getSessionById(1L));
    }

    @Test
    void testEndSessionSuccess() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        sessionService.endSession(1L);

        verify(computerService, times(1)).markAsAvailable(1L);
    }

    @Test
    void testEndSessionNonActive() {
        testSession.setStatus(Session.SessionStatus.ENDED);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        assertThrows(BusinessRuleException.class, () -> sessionService.endSession(1L));
    }
}
