package com.sigma_squad.computify.service;

import com.sigma_squad.computify.dto.UserDTO;
import com.sigma_squad.computify.entity.User;
import com.sigma_squad.computify.exception.BusinessRuleException;
import com.sigma_squad.computify.exception.ResourceNotFoundException;
import com.sigma_squad.computify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService - Answers: "What can users do?"
 * Handles user management, validation, and role-based rules.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Business rule: Email must end in @students.nu-laguna.edu.ph
     * Business rule: StudentId format must be YYYY-[5-7 digits]
     * Business rule: studentId required for STUDENT role only
     */
    public User createUser(String name, String studentId, String email, String passwordHash, Boolean isAdmin) {
        // Email domain validation
        if (!email.endsWith("@students.nu-laguna.edu.ph")) {
            throw new BusinessRuleException("Email must end with @students.nu-laguna.edu.ph");
        }

        // studentId validation for non-admin users
        if (!isAdmin) {
            if (studentId == null || studentId.isBlank()) {
                throw new BusinessRuleException("studentId is required for student users");
            }
            
            // Validate studentId format: YYYY-[5-7 digits]
            if (!studentId.matches("^\\d{4}-\\d{5,7}$")) {
                throw new BusinessRuleException("Invalid studentId format. Must be YEAR-[5-7 digits], e.g., 2025-12345");
            }
        }

        // Unique constraints
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("Email already exists");
        }

        if (!isAdmin && userRepository.existsByStudentId(studentId)) {
            throw new BusinessRuleException("Student ID already exists");
        }

        User user = new User();
        user.setName(name);
        user.setStudentId(studentId);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setIsAdmin(isAdmin);

        return userRepository.save(user);
    }

    /**
     * Find user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Find user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Find user by student ID
     */
    public User getUserByStudentId(String studentId) {
        return userRepository.findByStudentId(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with studentId: " + studentId));
    }

    /**
     * Convert entity to DTO (prevent entity leakage)
     */
    public UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getStudentId(),
            user.getEmail(),
            user.getIsAdmin(),
            user.getCreatedAt()
        );
    }

    /**
     * Get all users (admin only - to be enforced at controller level)
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
