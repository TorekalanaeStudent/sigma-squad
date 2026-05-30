package com.sigma_squad.computify.extension.controller;

import com.sigma_squad.computify.extension.dto.ExtensionRequestDTO;
import com.sigma_squad.computify.extension.entity.ExtensionRequest;
import com.sigma_squad.computify.extension.service.IExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final IExtensionService extensionService;

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<ExtensionRequestDTO> requestExtension(
            @PathVariable Long sessionId,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = (Long) authentication.getPrincipal();
        ExtensionRequest extensionRequest = extensionService.createExtensionRequest(sessionId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(extensionService.toDTO(extensionRequest));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ExtensionRequestDTO>> getPendingExtensionRequests() {
        List<ExtensionRequest> extensionRequests = extensionService.getPendingExtensionRequests();
        return ResponseEntity.ok(extensionRequests.stream()
                .map(extensionService::toDTO)
                .toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExtensionRequestDTO>> getUserExtensionRequests(@PathVariable Long userId) {
        List<ExtensionRequest> extensionRequests = extensionService.getUserExtensionRequests(userId);
        return ResponseEntity.ok(extensionRequests.stream()
                .map(extensionService::toDTO)
                .toList());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ExtensionRequestDTO> approveExtensionRequest(@PathVariable Long id) {
        extensionService.approveExtensionRequest(id);
        ExtensionRequest extensionRequest = extensionService.getExtensionRequestById(id);
        return ResponseEntity.ok(extensionService.toDTO(extensionRequest));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ExtensionRequestDTO> rejectExtensionRequest(@PathVariable Long id) {
        extensionService.rejectExtensionRequest(id);
        ExtensionRequest extensionRequest = extensionService.getExtensionRequestById(id);
        return ResponseEntity.ok(extensionService.toDTO(extensionRequest));
    }
}
