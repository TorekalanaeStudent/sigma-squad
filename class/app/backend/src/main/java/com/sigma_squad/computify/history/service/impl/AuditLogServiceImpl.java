package com.sigma_squad.computify.history.service.impl;

import com.sigma_squad.computify.history.entity.AuditLog;
import com.sigma_squad.computify.history.dto.AuditLogDTO;
import com.sigma_squad.computify.history.repository.AuditLogRepository;
import com.sigma_squad.computify.history.service.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements IAuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(Long userId, Long reservationId, String action, String details) {
        AuditLog log = AuditLog.builder()
            .userId(userId)
            .reservationId(reservationId)
            .action(action)
            .timestamp(Instant.now())
            .details(details)
            .build();
        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLogDTO> getUserHistory(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId)
            .stream()
            .map(AuditLogDTO::fromEntity)
            .toList();
    }

    @Override
    public List<AuditLogDTO> getReservationHistory(Long reservationId) {
        return auditLogRepository.findByReservationId(reservationId)
            .stream()
            .map(AuditLogDTO::fromEntity)
            .toList();
    }
}
