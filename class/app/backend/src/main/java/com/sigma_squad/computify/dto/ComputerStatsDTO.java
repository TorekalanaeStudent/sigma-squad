package com.sigma_squad.computify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComputerStatsDTO {
    private long totalComputers;
    private long availableComputers;
    private long reservedComputers;
    private long inUseComputers;
    private long outOfServiceComputers;
}
