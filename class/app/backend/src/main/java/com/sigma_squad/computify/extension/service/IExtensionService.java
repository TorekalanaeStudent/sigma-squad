package com.sigma_squad.computify.extension.service;

import com.sigma_squad.computify.extension.dto.ExtensionRequestDTO;
import com.sigma_squad.computify.extension.entity.ExtensionRequest;

import java.util.List;

public interface IExtensionService {
    ExtensionRequest createExtensionRequest(Long sessionId, Long userId);
    ExtensionRequest getExtensionRequestById(Long id);
    List<ExtensionRequest> getPendingExtensionRequests();
    List<ExtensionRequest> getUserExtensionRequests(Long userId);
    void approveExtensionRequest(Long id);
    void rejectExtensionRequest(Long id);
    ExtensionRequestDTO toDTO(ExtensionRequest extensionRequest);
}
