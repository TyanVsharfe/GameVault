package com.gamevault.listener;

import com.gamevault.dto.input.GeneratedEmailToken;
import com.gamevault.enums.Enums;
import com.gamevault.events.UserRegisteredEvent;
import com.gamevault.events.UserResetPasswordEvent;
import com.gamevault.events.VerificationEmailMessage;
import com.gamevault.service.email.EmailTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@Slf4j
public class VerificationEmailListener {

    private final EmailTokenService emailTokenService;
    private final KafkaTemplate<String, VerificationEmailMessage> kafkaTemplate;

    public VerificationEmailListener(EmailTokenService emailTokenService, KafkaTemplate<String, VerificationEmailMessage> kafkaTemplate) {
        this.emailTokenService = emailTokenService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        send(event.userId(), Enums.TokenType.EMAIL_VERIFICATION);
    }

    @EventListener
    public void handle(UserResetPasswordEvent event) {
        send(event.userId(), Enums.TokenType.PASSWORD_RESET);
    }

    private void send(UUID userId, Enums.TokenType tokenType) {
        log.info("Sending {} token for user {}", tokenType, userId);
        try {
            GeneratedEmailToken token = emailTokenService.createToken(userId, tokenType);

            kafkaTemplate.send(
                    "email-verification-topic",
                    new VerificationEmailMessage(
                            token.user().getEmail(),
                            token.rawToken(),
                            tokenType
                    )
            ).whenComplete((emailVerificationToken, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to send Kafka message for user {}: {}",
                            userId, throwable.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error while sending email token for registration user: {}", userId, e);
        }
    }
}
