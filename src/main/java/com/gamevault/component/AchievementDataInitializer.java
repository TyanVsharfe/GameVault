package com.gamevault.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.AchievementTranslation;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.db.repository.achievement.AchievementRepository;
import com.gamevault.db.repository.achievement.UserAchievementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AchievementDataInitializer {

    @Bean
    public CommandLineRunner initAchievements(
            AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            UserRepository userRepository
    ) {
        return args -> {
            if (achievementRepository.count() == 0) {
                List<Achievement> achievements = loadAchievementsFromJson();
                achievementRepository.saveAll(achievements);

                Iterable<User> users = userRepository.findAll();
                List<Achievement> allAchievements = (List<Achievement>) achievementRepository.findAll();

                for (User user : users) {
                    if (userAchievementRepository.countByUser(user) == 0) {
                        List<UserAchievement> userAchievements = allAchievements.stream()
                                .map(a -> new UserAchievement(user, a))
                                .collect(Collectors.toList());
                        userAchievementRepository.saveAll(userAchievements);
                    }
                }
            }
        };
    }

    private List<Achievement> loadAchievementsFromJson() throws IOException {
        ClassPathResource resource = new ClassPathResource("achievements.json");
        List<AchievementDto> dtos = new ObjectMapper().readValue(resource.getInputStream(), new TypeReference<>() {});
        return dtos.stream().map(this::toAchievement).collect(Collectors.toList());
    }

    private Achievement toAchievement(AchievementDto dto) {
        Achievement achievement = new Achievement(Enums.AchievementCategory.valueOf(dto.category), dto.requiredCount, dto.iconUrl, dto.exp);
        AchievementTranslation ruTranslation = new AchievementTranslation("ru", dto.ru.name, dto.ru.description, achievement);
        AchievementTranslation enTranslation = new AchievementTranslation("en", dto.en.name, dto.en.description, achievement);
        achievement.getTranslations().add(ruTranslation);
        achievement.getTranslations().add(enTranslation);
        return achievement;
    }

    public record AchievementDto(
            String category,
            int requiredCount,
            String iconUrl,
            int exp,
            TranslationDto ru,
            TranslationDto en
    ) {}

    public record TranslationDto(
            String name,
            String description
    ) {}
}
