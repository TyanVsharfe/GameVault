package com.gamevault.service.achievement;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.AchievementTranslation;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.db.repository.achievement.AchievementRepository;
import com.gamevault.db.repository.achievement.UserAchievementRepository;
import com.gamevault.dto.output.achievement.AchievementDTO;
import com.gamevault.dto.output.achievement.UserAchievementDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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

    public Iterable<UserAchievementDTO> getUserAchievements(UUID userId, String lang) {
        List<UserAchievement> userAchievements = userAchievementRepository.findUserAchievementsByUser_Id(userId);

        return userAchievements.stream().map(a -> {
            List<AchievementTranslation> translations = a.getAchievement().getTranslations();
            Optional<AchievementTranslation> optionalTranslation = translations.stream()
                    .filter(tr -> tr.getLanguage().equals(lang))
                    .findFirst();

            AchievementTranslation tr = optionalTranslation.orElseGet(() ->
                    translations.stream()
                            .filter(t -> t.getLanguage().equals("en"))
                            .findFirst()
                            .orElse(translations.get(0))
            );

            AchievementDTO achievementDTO = new AchievementDTO(
                    a.getAchievement().getId(),
                    tr.getName(),
                    tr.getDescription(),
                    a.getAchievement().getCategory().name(),
                    a.getAchievement().getExperiencePoints(),
                    a.getAchievement().getRequiredCount(),
                    a.getAchievement().getIconUrl()
            );
            return new UserAchievementDTO(
                    a.getId(),
                    achievementDTO,
                    a.getCurrentProgress(),
                    a.getIsCompleted()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Transactional
    public void initializeUserAchievements(User user) {
        List<Achievement> allAchievements = (List<Achievement>) achievementRepository.findAll();
        if (userAchievementRepository.countByUser(user) == 0) {
            log.info("No achievements found for user '{}'. Assigning default achievements.", user.getUsername());
            List<UserAchievement> userAchievements = allAchievements.stream()
                    .map(achievement -> new UserAchievement(user, achievement))
                    .collect(Collectors.toList());
            userAchievementRepository.saveAll(userAchievements);
            log.info("Default achievements assigned to user '{}'.", user.getUsername());
        }
        else {
            log.info("User '{}' already has achievements assigned.", user.getUsername());
        }
    }

    @Transactional
    public void updateAchievementProgress(UUID userId, Long achievementId, Integer progress) {
        Optional<UserAchievement> userAchievementOpt = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId);

        if (userAchievementOpt.isPresent()) {
            UserAchievement userAchievement = userAchievementOpt.get();
            userAchievement.setCurrentProgress(progress);

            if (progress >= userAchievement.getAchievement().getRequiredCount() && !userAchievement.getIsCompleted()) {
                userAchievement.setIsCompleted(true);
                userAchievement.setAchievedAt(Instant.now());
            }

            userAchievementRepository.save(userAchievement);
            return;
        }

        throw new RuntimeException("User achievement not found");
    }
}
