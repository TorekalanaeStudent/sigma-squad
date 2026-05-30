package com.sigma_squad.computify.service;

import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.auth.repository.UserRepository;
import com.sigma_squad.computify.auth.service.IUserService;
import com.sigma_squad.computify.auth.service.impl.UserServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@students.nu-laguna.edu.ph")
                .studentId("2023-12345")
                .passwordHash("hashed_password")
                .isAdmin(false)
                .build();
    }

    @Test
    void testCreateUserSuccess() {
        // Given
        when(userRepository.existsByEmail("john@students.nu-laguna.edu.ph")).thenReturn(false);
        when(userRepository.existsByStudentId("2023-12345")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser("John Doe", "2023-12345", "john@students.nu-laguna.edu.ph", "hashed_password", false);

        // Then
        assertNotNull(result);
        assertEquals("john@students.nu-laguna.edu.ph", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserInvalidStudentIdFormat() {
        // When & Then - Invalid format without dash
        assertThrows(BusinessRuleException.class, () ->
            userService.createUser("John Doe", "202312345", "john@students.nu-laguna.edu.ph", "hashed_password", false)
        );
    }

    @Test
    void testCreateUserInvalidStudentIdTooFewDigits() {
        // When & Then - Invalid format with only 4 digits after year
        assertThrows(BusinessRuleException.class, () ->
            userService.createUser("John Doe", "2023-1234", "john@students.nu-laguna.edu.ph", "hashed_password", false)
        );
    }

    @Test
    void testCreateUserInvalidStudentIdTooManyDigits() {
        // When & Then - Invalid format with 8 digits after year
        assertThrows(BusinessRuleException.class, () ->
            userService.createUser("John Doe", "2023-12345678", "john@students.nu-laguna.edu.ph", "hashed_password", false)
        );
    }

    @Test
    void testCreateUserValidStudentIdWith5Digits() {
        // Given
        when(userRepository.existsByEmail("john@students.nu-laguna.edu.ph")).thenReturn(false);
        when(userRepository.existsByStudentId("2023-12345")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser("John Doe", "2023-12345", "john@students.nu-laguna.edu.ph", "hashed_password", false);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserValidStudentIdWith7Digits() {
        // Given
        when(userRepository.existsByEmail("john@students.nu-laguna.edu.ph")).thenReturn(false);
        when(userRepository.existsByStudentId("2023-1234567")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser("John Doe", "2023-1234567", "john@students.nu-laguna.edu.ph", "hashed_password", false);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserMissingStudentIdForStudent() {
        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            userService.createUser("John Doe", null, "john@students.nu-laguna.edu.ph", "hashed_password", false)
        );
    }

    @Test
    void testCreateUserDuplicateEmail() {
        // Given
        when(userRepository.existsByEmail("john@students.nu-laguna.edu.ph")).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () ->
            userService.createUser("John Doe", "2023-12345", "john@students.nu-laguna.edu.ph", "hashed_password", false)
        );
    }

    @Test
    void testGetUserByEmailSuccess() {
        // Given
        when(userRepository.findByEmail("john@students.nu-laguna.edu.ph")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserByEmail("john@students.nu-laguna.edu.ph");

        // Then
        assertNotNull(result);
        assertEquals("john@students.nu-laguna.edu.ph", result.getEmail());
    }

    @Test
    void testGetUserByEmailNotFound() {
        // Given
        when(userRepository.findByEmail("notfound@students.nu-laguna.edu.ph")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserByEmail("notfound@students.nu-laguna.edu.ph")
        );
    }
}
