package com.sigma_squad.computify.auth.service;

import com.sigma_squad.computify.auth.dto.AuthResponse;
import com.sigma_squad.computify.auth.dto.LoginRequest;
import com.sigma_squad.computify.auth.dto.RegisterRequest;

/**
 * IAuthService - Contract for authentication operations
 */
public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
