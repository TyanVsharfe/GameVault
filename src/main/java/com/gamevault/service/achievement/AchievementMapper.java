package com.gamevault.service.achievement;

import com.gamevault.db.model.achievement.*;
import com.gamevault.dto.output.achievement.AchievementDto;
import com.gamevault.dto.output.achievement.AchievementGameDto;
import com.gamevault.dto.output.achievement.SeriesPartDto;
import com.gamevault.service.integration.IgdbGameService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class AchievementMapper {

    public AchievementMapper(IgdbGameService igdbGameService) {
    }

    public AchievementDto toDto(
            Achievement achievement,
            AchievementTranslation tr,
            Set<Long> completedGameIds,
            Map<Long, JsonNode> gamesById
    ) {
        if (achievement instanceof CountAchievement count) {
            return toCountDto(count, tr);
        }

        if (achievement instanceof SeriesAchievement series) {
            return toSeriesDto(series, tr, completedGameIds, gamesById);
        }

        throw new IllegalStateException("Unknown achievement type");
    }

    private AchievementDto toCountDto(
            CountAchievement achievement,
            AchievementTranslation tr
    ) {
        return new AchievementDto(
                achievement.getId(),
                tr.getName(),
                tr.getDescription(),
                achievement.getCategory().name(),
                achievement.getExperiencePoints(),
                achievement.getRequiredCount(),
                achievement.getIconUrl(),
                null
        );
    }

    private AchievementDto toSeriesDto(
            SeriesAchievement achievement,
            AchievementTranslation tr,
            Set<Long> completedGameIds,
            Map<Long, JsonNode> gamesById
    ) {
        List<SeriesPartDto> parts = achievement.getRequiredGames()
                .stream()
                .map(part -> toSeriesPartDto(part, completedGameIds, gamesById))
                .toList();

        return new AchievementDto(
                achievement.getId(),
                tr.getName(),
                tr.getDescription(),
                achievement.getCategory().name(),
                achievement.getExperiencePoints(),
                parts.size(),
                achievement.getIconUrl(),
                parts
        );
    }

    private SeriesPartDto toSeriesPartDto(
            SeriesPart part,
            Set<Long> completedGameIds,
            Map<Long, JsonNode> gamesById
    ) {
        List<AchievementGameDto> games = part.getGameIds().stream()
                .map(gamesById::get)
                .filter(Objects::nonNull)
                .map(this::toGameDto)
                .toList();

//        List<AchievementGameDto> games =
//                igdbGameService.getGamesByIds(part.getGameIds().stream().toList())
//                        .stream()
//                        .map(this::toGameDto)
//                        .toList();

        System.out.println("part.getGameIds(): " + part.getGameIds());
        System.out.println("completedGameIds: " + completedGameIds);
        for (Long id : completedGameIds) {
            System.out.println("Checking " + id + ": " + part.getGameIds().contains(id));
        }

        boolean completed = completedGameIds.stream().anyMatch(x -> part.getGameIds().contains(x));

        return new SeriesPartDto(
                part.getId(),
                new HashSet<>(games),
                completed
        );
    }

    private AchievementGameDto toGameDto(JsonNode game) {
        return new AchievementGameDto(
                game.get("id").asLong(),
                game.get("name").asText(),
                game.path("cover").path("url").asText(null),
                extractYear(game.path("first_release_date"))
        );
    }

    public static Integer extractYear(JsonNode node) {
        if (node == null || node.isNull()) return null;

        return Instant.ofEpochSecond(node.asLong())
                .atZone(ZoneId.systemDefault())
                .getYear();
    }

    public Set<Long> collectAllGameIds(List<Achievement> achievements) {
        return achievements.stream()
                .filter(achievement -> achievement instanceof SeriesAchievement)
                .flatMap(achievement -> ((SeriesAchievement) achievement).getRequiredGames().stream())
                .flatMap(seriesPart -> seriesPart.getGameIds().stream())
                .collect(Collectors.toSet());
    }
}
