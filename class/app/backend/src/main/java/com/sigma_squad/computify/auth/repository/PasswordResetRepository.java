package com.sigma_squad.computify.auth.repository;

import com.sigma_squad.computify.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
    void deleteByUserId(Long userId);
}
