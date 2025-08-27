package com.gamevault.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.db.model.achievement.*;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.db.repository.achievement.AchievementRepository;
import com.gamevault.db.repository.achievement.UserAchievementRepository;
import com.gamevault.service.achievement.AchievementProcessorService;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AchievementDataInitializer {

    @Bean
    public CommandLineRunner initAchievements(
            AchievementRepository achievementRepository,
            AchievementProcessorService achievementProcessorService,
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
                        achievementProcessorService.processAchievementCompletion(user);
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
        Achievement achievement;
        switch (dto.category) {
            case("TOTAL_GAMES_COMPLETED") -> {
                assert dto.requiredCount != null;
                achievement = new CountAchievement(Enums.AchievementCategory.valueOf(dto.category), dto.requiredCount, dto.iconUrl, dto.exp);
            }
            case("SERIES_COMPLETED") -> {
                assert dto.requiredGameIds != null;
                List<SeriesPart> seriesParts = new ArrayList<>(dto.requiredGameIds.size());
                for (Set<Long> part: Objects.requireNonNull(dto.requiredGameIds())) {
                    seriesParts.add(new SeriesPart(part));
                }
                achievement = new SeriesAchievement(Enums.AchievementCategory.valueOf(dto.category), seriesParts, dto.iconUrl, dto.exp);
            }
            default -> throw new IllegalArgumentException("Category not exist");
        }
        AchievementTranslation ruTranslation = new AchievementTranslation("ru", dto.ru.name, dto.ru.description, achievement);
        AchievementTranslation enTranslation = new AchievementTranslation("en", dto.en.name, dto.en.description, achievement);
        achievement.getTranslations().add(ruTranslation);
        achievement.getTranslations().add(enTranslation);
        return achievement;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AchievementDto(
            @Enumerated(EnumType.STRING) String category,
            @Nullable Integer requiredCount,
            @Nullable List<Set<Long>> requiredGameIds,
            @NotNull String iconUrl,
            int exp,
            @NotNull TranslationDto ru,
            @NotNull TranslationDto en
    ) {}

    public record TranslationDto(
            String name,
            String description
    ) {}
}
