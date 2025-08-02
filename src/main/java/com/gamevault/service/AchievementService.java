package com.gamevault.service;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.db.repository.AchievementRepository;
import com.gamevault.db.repository.UserAchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    public AchievementService(AchievementRepository achievementRepository, UserAchievementRepository userAchievementRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    public Iterable<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public Iterable<Achievement> getAchievementsByCategory(Enums.AchievementCategory category) {
        return achievementRepository.findByCategory(category);
    }

    public Iterable<UserAchievement> getUserAchievements(UUID userId) {
        return userAchievementRepository.findByUserId(userId);
    }

    @Transactional
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Transactional
    public void initializeUserAchievements(User user) {
        List<Achievement> allAchievements = (List<Achievement>) achievementRepository.findAll();

        List<UserAchievement> userAchievements = allAchievements.stream()
                .map(achievement -> new UserAchievement(user, achievement))
                .collect(Collectors.toList());

        userAchievementRepository.saveAll(userAchievements);
    }

    @Transactional
    public void updateAchievementProgress(UUID userId, Long achievementId, Integer progress) {
        Optional<UserAchievement> userAchievementOpt = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId);

        if (userAchievementOpt.isPresent()) {
            UserAchievement userAchievement = userAchievementOpt.get();
            userAchievement.setCurrentProgress(progress);

            if (progress >= userAchievement.getAchievement().getRequiredCount() && !userAchievement.getCompleted()) {
                userAchievement.setCompleted(true);
                userAchievement.setAchievedAt(Instant.now());
            }

            userAchievementRepository.save(userAchievement);
            return;
        }

        throw new RuntimeException("User achievement not found");
    }
}
