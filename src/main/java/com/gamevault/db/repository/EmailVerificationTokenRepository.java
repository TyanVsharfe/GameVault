package com.gamevault.db.repository;

import com.gamevault.db.model.EmailVerificationToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from EmailVerificationToken t where t.token = :token")
    Optional<EmailVerificationToken> findByTokenForUpdate(String token);
}
