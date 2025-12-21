package com.gamevault.service.achievement;

import com.gamevault.db.model.achievement.CountAchievement;
import com.gamevault.db.model.achievement.SeriesAchievement;
import com.gamevault.dto.input.achievement.AchievementForm;
import com.gamevault.dto.input.achievement.AchievementTranslationForm;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.achievement.Achievement;
import com.gamevault.db.model.achievement.AchievementTranslation;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.db.repository.achievement.AchievementRepository;
import com.gamevault.db.repository.achievement.UserAchievementRepository;
import com.gamevault.dto.output.achievement.AchievementDto;
import com.gamevault.dto.output.achievement.UserAchievementDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    public Iterable<UserAchievementDto> getUserAchievements(UUID userId, String lang) {
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

            AchievementDto achievementDTO = null;
            if (a.getAchievement() instanceof CountAchievement countAchievement) {
                achievementDTO = new AchievementDto(
                        a.getAchievement().getId(),
                        tr.getName(),
                        tr.getDescription(),
                        a.getAchievement().getCategory().name(),
                        a.getAchievement().getExperiencePoints(),
                        countAchievement.getRequiredCount(),
                        a.getAchievement().getIconUrl()
                );
            }
            if (a.getAchievement() instanceof SeriesAchievement seriesAchievement) {
                achievementDTO = new AchievementDto(
                        a.getAchievement().getId(),
                        tr.getName(),
                        tr.getDescription(),
                        a.getAchievement().getCategory().name(),
                        a.getAchievement().getExperiencePoints(),
                        seriesAchievement.getRequiredGameIds().size(),
                        a.getAchievement().getIconUrl()
                );
            }

            return new UserAchievementDto(
                    a.getId(),
                    achievementDTO,
                    a.getCurrentProgress(),
                    a.getIsCompleted()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public Achievement createAchievement(AchievementForm achievementForm) {
        Achievement achievement;
        switch (achievementForm.category().name()) {
            case("TOTAL_GAMES_COMPLETED") ->
                    achievement = new CountAchievement(
                            achievementForm.category(), achievementForm.requiredCount(), achievementForm.iconUrl(), achievementForm.exp());
            case("SERIES_COMPLETED") ->
                    achievement = new SeriesAchievement(
                            achievementForm.category(), achievementForm.requiredGameIds(), achievementForm.iconUrl(), achievementForm.exp());
            default -> throw new IllegalArgumentException("Category not exist");
        }
        for (AchievementTranslationForm atf: achievementForm.translations()) {
            AchievementTranslation achievementTranslation = new AchievementTranslation(
                    atf.language(), atf.name(), atf.description(), achievement);
            achievement.getTranslations().add(achievementTranslation);
        }

        Achievement saved = achievementRepository.save(achievement);
        log.info("Achievement with Id={} successfully added", saved.getId());
        return saved;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAchievementProgress(UUID userId, Long achievementId, Integer progress) {
        Optional<UserAchievement> userAchievementOpt = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId);

        if (userAchievementOpt.isPresent()) {
            UserAchievement userAchievement = userAchievementOpt.get();
            int oldProgress = userAchievement.getCurrentProgress();
            userAchievement.setCurrentProgress(progress);
            Achievement achievement = userAchievement.getAchievement();

            if (achievement instanceof CountAchievement countAchievement) {
                if (progress >= countAchievement.getRequiredCount() && !userAchievement.getIsCompleted()) {
                    userAchievement.setIsCompleted(true);
                    userAchievement.setAchievedAt(Instant.now());
                }
            }
            if (achievement instanceof SeriesAchievement seriesAchievement) {
                if (progress >= seriesAchievement.getRequiredGameIds().size() && !userAchievement.getIsCompleted()) {
                    userAchievement.setIsCompleted(true);
                    userAchievement.setAchievedAt(Instant.now());
                }
            }

            log.info("Achievement with Id={} successfully updated: old - {}; new - {}", achievementId, oldProgress, progress);
            userAchievementRepository.save(userAchievement);
            return;
        }

        throw new RuntimeException("User achievement not found");
    }
}
