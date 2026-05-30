package com.sigma_squad.computify.extension.service.impl;

import com.sigma_squad.computify.extension.dto.ExtensionRequestDTO;
import com.sigma_squad.computify.extension.entity.ExtensionRequest;
import com.sigma_squad.computify.extension.repository.ExtensionRequestRepository;
import com.sigma_squad.computify.extension.service.IExtensionService;
import com.sigma_squad.computify.notification.service.INotificationService;
import com.sigma_squad.computify.session.entity.Session;
import com.sigma_squad.computify.session.service.ISessionService;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtensionServiceImpl implements IExtensionService {

    private final ExtensionRequestRepository extensionRequestRepository;
    private final ISessionService sessionService;
    private final INotificationService notificationService;

    @Override
    public ExtensionRequest createExtensionRequest(Long sessionId, Long userId) {
        Session session = sessionService.getSessionById(sessionId);
        
        if (!session.isActive()) {
            throw new BusinessRuleException("Session is not active");
        }

        if (extensionRequestRepository.findBySessionIdAndStatus(sessionId, ExtensionRequest.ExtensionStatus.PENDING).isPresent()) {
            throw new BusinessRuleException("User already has a pending extension request for this session");
        }

        ExtensionRequest extensionRequest = ExtensionRequest.builder()
                .sessionId(sessionId)
                .userId(userId)
                .status(ExtensionRequest.ExtensionStatus.PENDING)
                .requestedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(ExtensionRequest.EXPIRY_DURATION_SECONDS))
                .build();

        ExtensionRequest savedRequest = extensionRequestRepository.save(extensionRequest);
        
        // Create notification for admin (using userId 1 as default admin for now)
        // In a real system, this would get the actual admin user
        notificationService.createNotification(savedRequest.getId(), 1L);

        return savedRequest;
    }

    @Override
    public ExtensionRequest getExtensionRequestById(Long id) {
        ExtensionRequest request = extensionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extension request not found with id: " + id));
        
        markAsExpiredIfNeeded(request);
        return request;
    }

    @Override
    public List<ExtensionRequest> getPendingExtensionRequests() {
        List<ExtensionRequest> requests = extensionRequestRepository.findByStatus(ExtensionRequest.ExtensionStatus.PENDING);
        requests.forEach(this::markAsExpiredIfNeeded);
        return requests;
    }

    @Override
    public List<ExtensionRequest> getUserExtensionRequests(Long userId) {
        return extensionRequestRepository.findByUserId(userId);
    }

    @Override
    public void approveExtensionRequest(Long id) {
        ExtensionRequest extensionRequest = getExtensionRequestById(id);
        
        if (!extensionRequest.isPending()) {
            throw new BusinessRuleException("Cannot approve non-pending extension request");
        }

        Session session = sessionService.getSessionById(extensionRequest.getSessionId());
        
        if (!session.isActive()) {
            throw new BusinessRuleException("Session is not active");
        }

        extensionRequest.setStatus(ExtensionRequest.ExtensionStatus.APPROVED);
        extensionRequest.setRespondedAt(Instant.now());
        extensionRequestRepository.save(extensionRequest);

        sessionService.extendSession(extensionRequest.getSessionId(), ExtensionRequest.EXTENSION_DURATION_MINUTES);
    }

    @Override
    public void rejectExtensionRequest(Long id) {
        ExtensionRequest extensionRequest = getExtensionRequestById(id);
        
        if (!extensionRequest.isPending()) {
            throw new BusinessRuleException("Cannot reject non-pending extension request");
        }

        extensionRequest.setStatus(ExtensionRequest.ExtensionStatus.REJECTED);
        extensionRequest.setRespondedAt(Instant.now());
        extensionRequestRepository.save(extensionRequest);
    }

    @Override
    public ExtensionRequestDTO toDTO(ExtensionRequest extensionRequest) {
        return ExtensionRequestDTO.fromEntity(extensionRequest);
    }

    private void markAsExpiredIfNeeded(ExtensionRequest request) {
        if (request.isExpired()) {
            request.setStatus(ExtensionRequest.ExtensionStatus.EXPIRED);
            extensionRequestRepository.save(request);
        }
    }
}

