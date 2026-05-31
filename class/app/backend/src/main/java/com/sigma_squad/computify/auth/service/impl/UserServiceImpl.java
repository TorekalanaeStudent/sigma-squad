package com.sigma_squad.computify.auth.service.impl;

import com.sigma_squad.computify.auth.service.IUserService;
import com.sigma_squad.computify.auth.dto.UserDTO;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.shared.exception.ResourceNotFoundException;
import com.sigma_squad.computify.auth.repository.UserRepository;
import com.sigma_squad.computify.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserServiceImpl - Implementation of IUserService
 * Handles user management, validation, and role-based rules.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(String name, String studentId, String email, String passwordHash, Boolean isAdmin) {
        if (!email.endsWith("@students.nu-laguna.edu.ph")) {
            throw new BusinessRuleException("Email must end with @students.nu-laguna.edu.ph");
        }

        if (!isAdmin) {
            if (studentId == null || studentId.isBlank()) {
                throw new BusinessRuleException("studentId is required for student users");
            }
            
            if (!studentId.matches("^\\d{4}-\\d{5,7}$")) {
                throw new BusinessRuleException("Invalid studentId format. Must be YEAR-[5-7 digits], e.g., 2025-12345");
            }
        }

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

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByStudentId(String studentId) {
        return userRepository.findByStudentId(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with studentId: " + studentId));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
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

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
