package com.gamevault.service.email;

import com.gamevault.events.VerificationEmailEvent;
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
    public void sendVerificationEmail(VerificationEmailEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            String verificationLink =
                    frontendUrl + "/verify?token=" + event.token();

            Context context = new Context();
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("email", event.email());

            helper.setFrom(senderEmail);
            helper.setTo(event.email());
            helper.setSubject("Verify your email address");

            String html = templateEngine.process("email/verification", context);
            String text = templateEngine.process("email/verification.txt", context);

            helper.setText(text, html);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Failed to send verification email", e);
        }
    }
}
