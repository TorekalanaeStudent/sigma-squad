package com.sigma_squad.computify.computer.service.impl;

import com.sigma_squad.computify.computer.dto.ComputerDTO;
import com.sigma_squad.computify.computer.entity.Computer;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComputerServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ComputerServiceImplTest {

    @Mock
    private ComputerRepository computerRepository;

    @InjectMocks
    private ComputerServiceImpl computerService;

    private Computer testComputer;

    @BeforeEach
    void setUp() {
        testComputer = new Computer();
        testComputer.setId(1L);
        testComputer.setComputerNumber(1);
        testComputer.setStatus(Computer.ComputerStatus.AVAILABLE);
    }

    @Test
    void testCreateComputerSuccess() {
        when(computerRepository.existsByComputerNumber(1)).thenReturn(false);
        when(computerRepository.save(any(Computer.class))).thenReturn(testComputer);

        Computer result = computerService.createComputer(1);

        assertNotNull(result);
        assertEquals(1, result.getComputerNumber());
        verify(computerRepository, times(1)).save(any(Computer.class));
    }

    @Test
    void testCreateComputerDuplicate() {
        when(computerRepository.existsByComputerNumber(1)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> computerService.createComputer(1));
    }

    @Test
    void testGetComputerByIdSuccess() {
        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));

        Computer result = computerService.getComputerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetComputerByIdNotFound() {
        when(computerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> computerService.getComputerById(1L));
    }

    @Test
    void testIsAvailableTrue() {
        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));

        boolean result = computerService.isAvailable(1L);

        assertTrue(result);
    }

    @Test
    void testMarkAsReservedSuccess() {
        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));
        when(computerRepository.save(any(Computer.class))).thenReturn(testComputer);

        computerService.markAsReserved(1L, 1L);

        assertEquals(Computer.ComputerStatus.RESERVED, testComputer.getStatus());
        verify(computerRepository, times(1)).save(any(Computer.class));
    }

    @Test
    void testMarkAsAvailable() {
        testComputer.setStatus(Computer.ComputerStatus.RESERVED);
        testComputer.setCurrentUserId(1L);

        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));
        when(computerRepository.save(any(Computer.class))).thenReturn(testComputer);

        computerService.markAsAvailable(1L);

        assertEquals(Computer.ComputerStatus.AVAILABLE, testComputer.getStatus());
        assertNull(testComputer.getCurrentUserId());
    }
}
