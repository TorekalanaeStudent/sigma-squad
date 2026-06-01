package com.sigma_squad.computify.stats.service.impl;

import com.sigma_squad.computify.computer.dto.ComputerStatsDTO;
import com.sigma_squad.computify.computer.service.IComputerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StatsServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private IComputerService computerService;

    @InjectMocks
    private StatsServiceImpl statsService;

    private ComputerStatsDTO testStats;

    @BeforeEach
    void setUp() {
        testStats = new ComputerStatsDTO(10L, 5L, 2L, 2L, 1L);
    }

    @Test
    void testGetComputerStatsSuccess() {
        when(computerService.getComputerStats()).thenReturn(testStats);

        ComputerStatsDTO result = statsService.getComputerStats();

        assertNotNull(result);
        assertEquals(10L, result.totalComputers());
        assertEquals(5L, result.availableComputers());
        assertEquals(2L, result.reservedComputers());
        assertEquals(2L, result.inUseComputers());
        assertEquals(1L, result.outOfServiceComputers());
        verify(computerService, times(1)).getComputerStats();
    }

    @Test
    void testGetComputerStatsReturnsZeroStats() {
        ComputerStatsDTO zeroStats = new ComputerStatsDTO(0L, 0L, 0L, 0L, 0L);
        when(computerService.getComputerStats()).thenReturn(zeroStats);

        ComputerStatsDTO result = statsService.getComputerStats();

        assertNotNull(result);
        assertEquals(0L, result.totalComputers());
        assertEquals(0L, result.availableComputers());
        verify(computerService, times(1)).getComputerStats();
    }

    @Test
    void testGetComputerStatsNotNull() {
        when(computerService.getComputerStats()).thenReturn(testStats);

        ComputerStatsDTO result = statsService.getComputerStats();

        assertNotNull(result);
        verify(computerService, times(1)).getComputerStats();
    }
}
