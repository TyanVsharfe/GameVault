package com.gamevault.service.user;

import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.dto.input.UserForm;
import com.gamevault.events.UserRegisteredEvent;
import com.gamevault.metrics.CustomMetrics;
import com.gamevault.service.achievement.AchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    private final AchievementService achievementService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final CustomMetrics customMetrics;

    public UserService(UserRepository userRepository, AchievementService achievementService, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher, CustomMetrics customMetrics) {
        this.userRepository = userRepository;
        this.achievementService = achievementService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.customMetrics = customMetrics;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getUser(UUID userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public void register(UserForm form) {
        log.info("Attempting to register user: {}", form.username());

        User user = new User(
                form.username(),
                passwordEncoder.encode(form.password()),
                List.of("ROLE_USER")
        );

        user.setEmail(form.email());

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("User or Email already exists");
        }

        eventPublisher.publishEvent(
                new UserRegisteredEvent(user.getId())
        );

        log.info("Registration for User with username '{}' in progress. Email confirmation link has been sent", form.username());
    }

    public void completeRegistration(User user) {
        try {
            log.info("Start complete registration for User '{}' started.", user.getUsername());
            user.createProfile();
            userRepository.save(user);
            achievementService.initializeUserAchievements(user);

            customMetrics.incrementUserAuth("registration");
            log.info("Complete registration for User '{}' completed successfully.", user.getUsername());
        } catch (Exception e) {
            log.warn("Complete registration for User '{}' completed with error: {}", user.getUsername(), e.getMessage());
            throw e;
        }
    }
}
