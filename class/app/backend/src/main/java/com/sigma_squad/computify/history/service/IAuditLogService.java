package com.sigma_squad.computify.history.service;

import com.sigma_squad.computify.history.dto.AuditLogDTO;

import java.util.List;

public interface IAuditLogService {
    void log(Long userId, Long reservationId, String action, String details);
    List<AuditLogDTO> getUserHistory(Long userId);
    List<AuditLogDTO> getReservationHistory(Long reservationId);
}
