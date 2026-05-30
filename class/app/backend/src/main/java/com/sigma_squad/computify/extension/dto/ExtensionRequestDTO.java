package com.sigma_squad.computify.extension.dto;

import com.sigma_squad.computify.extension.entity.ExtensionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtensionRequestDTO {
    private Long id;
    private Long sessionId;
    private Long userId;
    private String status;
    private Instant requestedAt;
    private Instant respondedAt;

    public static ExtensionRequestDTO fromEntity(ExtensionRequest request) {
        return ExtensionRequestDTO.builder()
                .id(request.getId())
                .sessionId(request.getSessionId())
                .userId(request.getUserId())
                .status(request.getStatus().toString())
                .requestedAt(request.getRequestedAt())
                .respondedAt(request.getRespondedAt())
                .build();
    }
}
