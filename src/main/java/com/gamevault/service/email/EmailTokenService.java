package com.gamevault.service.email;

import com.gamevault.db.model.EmailToken;
import com.gamevault.db.model.User;
import com.gamevault.db.repository.EmailTokenRepository;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.dto.input.GeneratedEmailToken;
import com.gamevault.enums.Enums;
import com.gamevault.service.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class EmailTokenService {

    private final UserRepository userRepository;
    private final EmailTokenRepository emailTokenRepository;
    private  final UserService userService;

    public EmailTokenService(PasswordEncoder passwordEncoder, UserRepository userRepository, EmailTokenRepository emailTokenRepository, UserService userService) {
        this.userRepository = userRepository;
        this.emailTokenRepository = emailTokenRepository;
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GeneratedEmailToken createToken(UUID userId, Enums.TokenType tokenType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        String tokenValue = UUID.randomUUID().toString();
        String hashedToken = sha256(tokenValue);

        Instant expiresAt = Instant.now().plus(tokenType.getTtl());

        EmailToken token = new EmailToken(hashedToken, user, tokenType, expiresAt);
        token = emailTokenRepository.save(token);
        return new GeneratedEmailToken(token.getUser(), tokenValue);
    }

    @Transactional
    public void registrationVerify(String tokenValue) {
        String hashedToken = sha256(tokenValue);
        EmailToken token = getToken(hashedToken);

        User user = token.getUser();
        user.setEnabled(true);
        userService.completeRegistration(user);
        token.setUsed(true);
        emailTokenRepository.save(token);
    }

    @Transactional
    public void resetPasswordVerify(String tokenValue, String newPassword) {
        String hashedToken = sha256(tokenValue);
        EmailToken token = getToken(hashedToken);

        User user = token.getUser();
        user.setEnabled(true);
        userService.completeResetPassword(user, newPassword);
        token.setUsed(true);
        emailTokenRepository.save(token);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private EmailToken getToken(String hashedToken) {
        EmailToken token = emailTokenRepository
                .findByTokenForUpdate(hashedToken)
                .orElseThrow(() -> new IllegalStateException("Invalid token"));

        if (token.isUsed())
            throw new IllegalStateException("Token already used");

        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalStateException("Token expired");

        return token;
    }
}
