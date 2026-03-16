package com.gamevault.service.email;

import com.gamevault.events.VerificationEmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailVerifyConsumer {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.frontend-url}")
    private String frontendUrl;
    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailVerifyConsumer(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @KafkaListener(topics = "email-verification-topic", containerFactory = "emailFactory")
    public void sendVerificationEmail(VerificationEmailMessage event) {
        switch (event.tokenType()) {
            case EMAIL_VERIFICATION -> sendRegistrationVerifyEmail(event);
            case PASSWORD_RESET -> sendResetPasswordEmail(event);
        }
    }

    private void sendRegistrationVerifyEmail(VerificationEmailMessage event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            String verificationLink =
                    frontendUrl + "/registration/verify?token=" + event.token();

            Context context = new Context();
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("email", event.email());

            helper.setFrom(senderEmail);
            helper.setTo(event.email());
            helper.setSubject("Verify your email address");

            String html = templateEngine.process("email/registration/verification", context);
            String text = templateEngine.process("email/registration/verification.txt", context);

            helper.setText(text, html);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Failed to send verification email", e);
        }
    }

    private void sendResetPasswordEmail(VerificationEmailMessage event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            String resetPasswordLink =
                    frontendUrl + "/reset-password/verify?token=" + event.token();

            Context context = new Context();
            context.setVariable("resetPasswordLink", resetPasswordLink);
            context.setVariable("email", event.email());

            helper.setFrom(senderEmail);
            helper.setTo(event.email());
            helper.setSubject("Reset your password");
            String html = templateEngine.process("email/reset-password/reset-password", context);
            String text = templateEngine.process("email/reset-password/reset-password.txt", context);

            helper.setText(text, html);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Failed to send verification email", e);
        }
    }
}
