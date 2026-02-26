package com.gamevault.listener;

import com.gamevault.db.model.EmailVerificationToken;
import com.gamevault.events.UserRegisteredEvent;
import com.gamevault.events.VerificationEmailEvent;
import com.gamevault.service.email.EmailVerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class UserRegisteredListener {

    private final EmailVerificationTokenService emailVerificationTokenService;
    private final KafkaTemplate<String, VerificationEmailEvent> kafkaTemplate;

    public UserRegisteredListener(EmailVerificationTokenService emailVerificationTokenService, KafkaTemplate<String, VerificationEmailEvent> kafkaTemplate) {
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        log.info("Attempting to send email token for registration user: {}", event.userId());
        EmailVerificationToken token =
                emailVerificationTokenService.createToken(event.userId());

        kafkaTemplate.send(
                "email-verification-topic",
                new VerificationEmailEvent(
                        token.getUser().getEmail(),
                        token.getToken()
                )
        );
    }
}
