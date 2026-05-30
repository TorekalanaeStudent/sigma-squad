package com.sigma_squad.computify.stats.service.impl;

import com.sigma_squad.computify.stats.service.IStatsService;
import com.sigma_squad.computify.computer.dto.ComputerStatsDTO;
import com.sigma_squad.computify.computer.service.IComputerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * StatsServiceImpl - Implementation of IStatsService
 * Handles statistics and analytics operations.
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements IStatsService {

    private final IComputerService computerService;

    @Override
    public ComputerStatsDTO getComputerStats() {
        return computerService.getComputerStats();
    }
}
