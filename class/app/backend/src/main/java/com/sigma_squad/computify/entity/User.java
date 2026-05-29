package com.sigma_squad.computify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

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
    private String studentId; // Format: YYYY-[5-7 digits], Required for STUDENT role only

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash; // Stored as BCrypt hash

    @Column(nullable = false)
    @Default
    private Boolean isAdmin = false; // true = LIBRARIAN, false = STUDENT

    @Column(nullable = false)
    @Default
    private Instant createdAt = Instant.now();

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d{4}-\\d{5,7}$");

    /**
     * Business rule: Email must end in @students.nu-laguna.edu.ph
     */
    public boolean isValidNUEmail() {
        return email != null && email.endsWith("@students.nu-laguna.edu.ph");
    }

    /**
     * Business rule: StudentId format must be YYYY-[5-7 digits]
     * Valid examples: 2025-12345, 2025-123456, 2025-1234567
     */
    public boolean isValidStudentId() {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        return STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    /**
     * Business rule: studentId required for non-admin users and must be valid format
     */
    public boolean isValidStudentRole() {
        return isAdmin || (studentId != null && !studentId.isBlank() && isValidStudentId());
    }
}
