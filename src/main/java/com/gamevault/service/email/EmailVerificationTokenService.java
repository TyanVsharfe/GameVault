package com.gamevault.service.email;

import com.gamevault.db.model.EmailVerificationToken;
import com.gamevault.db.model.User;
import com.gamevault.db.repository.EmailVerificationTokenRepository;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class EmailVerificationTokenService {
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private  final UserService userService;

    public EmailVerificationTokenService(UserRepository userRepository, EmailVerificationTokenRepository emailVerificationTokenRepository, UserService userService) {
        this.userRepository = userRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EmailVerificationToken createToken(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        String tokenValue = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        EmailVerificationToken token = new EmailVerificationToken(tokenValue, user, expiresAt);

        return emailVerificationTokenRepository.save(token);
    }

    @Transactional
    public void verify(String tokenValue) {

        EmailVerificationToken token = emailVerificationTokenRepository
                .findByTokenForUpdate(tokenValue)
                .orElseThrow(() -> new IllegalStateException("Invalid token"));

        if (token.isUsed())
            throw new IllegalStateException("Token already used");

        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalStateException("Token expired");

        User user = token.getUser();
        user.setEnabled(true);
        userService.completeRegistration(user);
        token.setUsed(true);
        emailVerificationTokenRepository.save(token);
    }
}
