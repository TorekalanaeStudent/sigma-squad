package com.sigma_squad.computify.auth.service;

import com.sigma_squad.computify.auth.dto.UserDTO;
import com.sigma_squad.computify.auth.entity.User;

import java.util.List;

/**
 * IUserService - Contract for user management operations
 */
public interface IUserService {
    User createUser(String name, String studentId, String email, String passwordHash, Boolean isAdmin);
    User getUserByEmail(String email);
    User getUserById(Long id);
    User getUserByStudentId(String studentId);
    UserDTO toDTO(User user);
    List<UserDTO> getAllUsers();
}
