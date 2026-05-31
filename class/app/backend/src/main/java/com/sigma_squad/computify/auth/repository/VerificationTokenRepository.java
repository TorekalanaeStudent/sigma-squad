package com.sigma_squad.computify.auth.repository;

import com.sigma_squad.computify.auth.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByCode(String code);
    void deleteByUserId(Long userId);
}
