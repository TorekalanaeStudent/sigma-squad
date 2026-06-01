package com.sigma_squad.computify.extension.service.impl;

import com.sigma_squad.computify.extension.dto.ExtensionRequestDTO;
import com.sigma_squad.computify.extension.entity.ExtensionRequest;
import com.sigma_squad.computify.extension.repository.ExtensionRequestRepository;
import com.sigma_squad.computify.notification.service.INotificationService;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExtensionServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ExtensionServiceImplTest {

    @Mock
    private ExtensionRequestRepository extensionRequestRepository;

    @Mock
    private ISessionService sessionService;

    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private ExtensionServiceImpl extensionService;

    private Session testSession;
    private ExtensionRequest testExtensionRequest;

    @BeforeEach
    void setUp() {
        testSession = new Session();
        testSession.setId(1L);
        testSession.setUserId(1L);
        testSession.setComputerId(1L);

        testExtensionRequest = ExtensionRequest.builder()
                .id(1L)
                .sessionId(1L)
                .userId(1L)
                .status(ExtensionRequest.ExtensionStatus.PENDING)
                .requestedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    void testCreateExtensionRequestSuccess() {
        testSession.setStatus(Session.SessionStatus.ACTIVE);
        when(sessionService.getSessionById(1L)).thenReturn(testSession);
        when(extensionRequestRepository.findBySessionIdAndStatus(1L, ExtensionRequest.ExtensionStatus.PENDING))
                .thenReturn(Optional.empty());
        when(extensionRequestRepository.save(any(ExtensionRequest.class))).thenReturn(testExtensionRequest);

        ExtensionRequest result = extensionService.createExtensionRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ExtensionRequest.ExtensionStatus.PENDING, result.getStatus());
        verify(extensionRequestRepository, times(1)).save(any(ExtensionRequest.class));
    }

    @Test
    void testCreateExtensionRequestSessionNotActive() {
        testSession.setStatus(Session.SessionStatus.ENDED);
        when(sessionService.getSessionById(1L)).thenReturn(testSession);

        assertThrows(BusinessRuleException.class, () -> extensionService.createExtensionRequest(1L, 1L));
        verify(extensionRequestRepository, never()).save(any(ExtensionRequest.class));
    }

    @Test
    void testCreateExtensionRequestAlreadyPending() {
        testSession.setStatus(Session.SessionStatus.ACTIVE);
        when(sessionService.getSessionById(1L)).thenReturn(testSession);
        when(extensionRequestRepository.findBySessionIdAndStatus(1L, ExtensionRequest.ExtensionStatus.PENDING))
                .thenReturn(Optional.of(testExtensionRequest));

        assertThrows(BusinessRuleException.class, () -> extensionService.createExtensionRequest(1L, 1L));
        verify(extensionRequestRepository, never()).save(any(ExtensionRequest.class));
    }

    @Test
    void testApproveExtensionRequestSuccess() {
        testSession.setStatus(Session.SessionStatus.ACTIVE);
        testExtensionRequest.setStatus(ExtensionRequest.ExtensionStatus.PENDING);
        
        when(extensionRequestRepository.findById(1L)).thenReturn(Optional.of(testExtensionRequest));
        when(sessionService.getSessionById(1L)).thenReturn(testSession);
        when(extensionRequestRepository.save(any(ExtensionRequest.class))).thenReturn(testExtensionRequest);

        extensionService.approveExtensionRequest(1L);

        verify(extensionRequestRepository, times(1)).findById(1L);
        verify(sessionService, times(1)).getSessionById(1L);
        verify(sessionService, times(1)).extendSession(1L, ExtensionRequest.EXTENSION_DURATION_MINUTES);
        verify(extensionRequestRepository, times(1)).save(any(ExtensionRequest.class));
    }

    @Test
    void testRejectExtensionRequestSuccess() {
        testExtensionRequest.setStatus(ExtensionRequest.ExtensionStatus.PENDING);
        
        when(extensionRequestRepository.findById(1L)).thenReturn(Optional.of(testExtensionRequest));
        when(extensionRequestRepository.save(any(ExtensionRequest.class))).thenReturn(testExtensionRequest);

        extensionService.rejectExtensionRequest(1L);

        verify(extensionRequestRepository, times(1)).findById(1L);
        verify(extensionRequestRepository, times(1)).save(any(ExtensionRequest.class));
    }

    @Test
    void testGetPendingExtensionRequests() {
        when(extensionRequestRepository.findByStatus(ExtensionRequest.ExtensionStatus.PENDING))
                .thenReturn(java.util.List.of(testExtensionRequest));

        java.util.List<ExtensionRequest> results = extensionService.getPendingExtensionRequests();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        verify(extensionRequestRepository, times(1)).findByStatus(ExtensionRequest.ExtensionStatus.PENDING);
    }
}
