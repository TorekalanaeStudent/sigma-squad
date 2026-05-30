package com.sigma_squad.computify.history.controller;

import com.sigma_squad.computify.history.dto.AuditLogDTO;
import com.sigma_squad.computify.history.service.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class AuditLogController {
    private final IAuditLogService auditLogService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogDTO>> getUserHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getUserHistory(userId));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<AuditLogDTO>> getReservationHistory(@PathVariable Long reservationId) {
        return ResponseEntity.ok(auditLogService.getReservationHistory(reservationId));
    }
}
