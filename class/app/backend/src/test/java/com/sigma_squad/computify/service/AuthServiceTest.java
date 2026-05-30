package com.sigma_squad.computify.service;

import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;
import com.sigma_squad.computify.auth.dto.UserDTO;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.auth.service.IAuthService;
import com.sigma_squad.computify.auth.service.IUserService;
import com.sigma_squad.computify.auth.service.impl.AuthServiceImpl;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

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
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

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

        testUserDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@students.nu-laguna.edu.ph")
                .studentId("2023-12345")
                .build();

        registerRequest = new RegisterRequest("John Doe", "2023-12345", "john@students.nu-laguna.edu.ph", "password123");
        loginRequest = new LoginRequest("john@students.nu-laguna.edu.ph", "password123");
    }

    @Test
    void testRegisterSuccess() {
        // Given
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userService.createUser(
            "John Doe", "2023-12345", "john@students.nu-laguna.edu.ph", "hashed_password", false
        )).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(1L, "john@students.nu-laguna.edu.ph")).thenReturn("jwt_token");
        when(userService.toDTO(testUser)).thenReturn(testUserDTO);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
        assertNotNull(response.getUser());
        verify(userService, times(1)).createUser(
            "John Doe", "2023-12345", "john@students.nu-laguna.edu.ph", "hashed_password", false
        );
    }

    @Test
    void testLoginSuccess() {
        // Given
        when(userService.getUserByEmail("john@students.nu-laguna.edu.ph")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "john@students.nu-laguna.edu.ph")).thenReturn("jwt_token");
        when(userService.toDTO(testUser)).thenReturn(testUserDTO);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
        assertNotNull(response.getUser());
    }

    @Test
    void testLoginInvalidPassword() {
        // Given
        when(userService.getUserByEmail("john@students.nu-laguna.edu.ph")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "hashed_password")).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () ->
            authService.login(new LoginRequest("john@students.nu-laguna.edu.ph", "wrongpassword"))
        );
    }
}
