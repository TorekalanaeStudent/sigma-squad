package com.sigma_squad.computify.audit.controller;

import com.sigma_squad.computify.audit.dto.SessionAuditDTO;
import com.sigma_squad.computify.audit.service.IAuditSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditSessionController {
    
    private final IAuditSessionService auditSessionService;

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionAuditDTO>> getAllSessions() {
        List<SessionAuditDTO> sessions = auditSessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }
}
