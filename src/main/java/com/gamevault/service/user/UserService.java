package com.gamevault.service.user;

import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.dto.input.UserForm;
import com.gamevault.service.achievement.AchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public UserService(UserRepository userRepository, AchievementService achievementService) {
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getUser(UUID userId) {
        return userRepository.findById(userId);
    }

    public String add(UserForm user) {
        log.info("Attempting to register user: {}", user.username());

        if (userRepository.findByUsername(user.username()).isPresent()) {
            log.warn("Username '{}' already exists.", user.username());
            return "Username already exists";
        }

        String bcryptPass = new BCryptPasswordEncoder().encode(user.password());
        User newUser = new User(user.username(), bcryptPass, List.of("ROLE_USER"));
        User saved = userRepository.save(newUser);
        log.info("User '{}' registered successfully.", user.username());

        achievementService.initializeUserAchievements(saved);
        return "User registered successfully";
    }
}
