package com.sigma_squad.computify.auth.service.impl;

import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;
import com.sigma_squad.computify.auth.dto.UserDTO;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.auth.service.IUserService;
import com.sigma_squad.computify.shared.exception.UnauthorizedException;
import com.sigma_squad.computify.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@students.nu-laguna.edu.ph");
        testUser.setPasswordHash("hashedPassword");
        testUser.setIsAdmin(false);

        testUserDTO = new UserDTO(1L, "Test User", "2025-12345", "test@students.nu-laguna.edu.ph", false, null);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("Test User", "2025-12345", "test@students.nu-laguna.edu.ph", "password");

        when(userService.createUser(anyString(), anyString(), anyString(), anyString(), anyBoolean()))
            .thenReturn(testUser);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(jwtTokenProvider.generateToken(1L, "test@students.nu-laguna.edu.ph")).thenReturn("token123");
        when(userService.toDTO(testUser)).thenReturn(testUserDTO);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token123", response.token());
        verify(userService, times(1)).createUser(anyString(), anyString(), anyString(), anyString(), eq(false));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("test@students.nu-laguna.edu.ph", "password");

        when(userService.getUserByEmail("test@students.nu-laguna.edu.ph")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "test@students.nu-laguna.edu.ph")).thenReturn("token123");
        when(userService.toDTO(testUser)).thenReturn(testUserDTO);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token123", response.token());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest request = new LoginRequest("test@students.nu-laguna.edu.ph", "wrongPassword");

        when(userService.getUserByEmail("test@students.nu-laguna.edu.ph")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
