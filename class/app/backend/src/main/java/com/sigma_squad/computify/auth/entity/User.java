package com.sigma_squad.computify.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.regex.Pattern;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String studentId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAdmin = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d{4}-\\d{5,7}$");

    public boolean isValidNUEmail() {
        return email != null && email.endsWith("@students.nu-laguna.edu.ph");
    }

    public boolean isValidStudentId() {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        return STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    public boolean isValidStudentRole() {
        return isAdmin || (studentId != null && !studentId.isBlank() && isValidStudentId());
    }
}
