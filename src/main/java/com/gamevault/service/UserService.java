package com.gamevault.service;

import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.db.repository.AchievementRepository;
import com.gamevault.db.repository.UserAchievementRepository;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.form.user.UserForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserAchievementRepository userAchievementRepository, AchievementRepository achievementRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.achievementRepository = achievementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public String addUser(UserForm user) {
        log.info("Attempting to register user: {}", user.username());

        if (userRepository.findByUsername(user.username()).isPresent()) {
            log.warn("Username '{}' already exists.", user.username());
            return "Username already exists";
        }

        String bcryptPass = passwordEncoder.encode(user.password());
        User newUser = new User(user.username(), bcryptPass, List.of("ROLE_USER"));
        userRepository.save(newUser);
        log.info("User '{}' registered successfully.", user.username());

        List<Achievement> allAchievements = (List<Achievement>) achievementRepository.findAll();
        if (userAchievementRepository.countByUser(newUser) == 0) {
            log.info("No achievements found for user '{}'. Assigning default achievements.", user.username());
            List<UserAchievement> userAchievements = allAchievements.stream()
                    .map(achievement -> new UserAchievement(newUser, achievement))
                    .collect(Collectors.toList());
            userAchievementRepository.saveAll(userAchievements);
            log.info("Default achievements assigned to user '{}'.", user.username());
        }
        else {
            log.info("User '{}' already has achievements assigned.", user.username());
        }
        return "User registered successfully";
    }
}
