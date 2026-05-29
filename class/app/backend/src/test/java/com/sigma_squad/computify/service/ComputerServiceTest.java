package com.sigma_squad.computify.service;

import com.sigma_squad.computify.entity.Computer;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.ResourceNotFoundException;
import com.sigma_squad.computify.repository.ComputerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComputerServiceTest {

    @Mock
    private ComputerRepository computerRepository;

    @InjectMocks
    private ComputerService computerService;

    private Computer testComputer;

    @BeforeEach
    void setUp() {
        testComputer = Computer.builder()
                .id(1L)
                .computerNumber(1)
                .status(Computer.ComputerStatus.AVAILABLE)
                .build();
    }

    @Test
    void testCreateComputerSuccess() {
        // Given
        when(computerRepository.existsByComputerNumber(1)).thenReturn(false);
        when(computerRepository.save(any(Computer.class))).thenReturn(testComputer);

        // When
        Computer result = computerService.createComputer(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getComputerNumber());
        assertEquals(Computer.ComputerStatus.AVAILABLE, result.getStatus());
        verify(computerRepository, times(1)).save(any(Computer.class));
    }

    @Test
    void testCreateComputerDuplicateNumber() {
        // Given
        when(computerRepository.existsByComputerNumber(1)).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            computerService.createComputer(1)
        );
    }

    @Test
    void testGetComputerByIdSuccess() {
        // Given
        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));

        // When
        Computer result = computerService.getComputerById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetComputerByIdNotFound() {
        // Given
        when(computerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            computerService.getComputerById(999L)
        );
    }

    @Test
    void testMarkAsReservedSuccess() {
        // Given
        when(computerRepository.findById(1L)).thenReturn(Optional.of(testComputer));
        when(computerRepository.save(any(Computer.class))).thenReturn(testComputer);

        // When
        computerService.markAsReserved(1L, 10L);

        // Then
        assertEquals(Computer.ComputerStatus.RESERVED, testComputer.getStatus());
        assertEquals(10L, testComputer.getCurrentUserId());
        verify(computerRepository, times(1)).save(testComputer);
    }

    @Test
    void testMarkAsReservedNotAvailable() {
        // Given
        Computer unavailableComputer = Computer.builder()
                .id(1L)
                .computerNumber(1)
                .status(Computer.ComputerStatus.IN_USE)
                .build();
        when(computerRepository.findById(1L)).thenReturn(Optional.of(unavailableComputer));

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            computerService.markAsReserved(1L, 10L)
        );
    }
}
