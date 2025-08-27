package com.gamevault.dto.input.achievement;

import com.gamevault.db.model.achievement.SeriesPart;
import com.gamevault.enums.Enums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AchievementForm(
        @NotNull List<AchievementTranslationForm> translations,
        @NotNull @Enumerated(EnumType.STRING) Enums.AchievementCategory category,
        @NotNull int exp,
        int requiredCount,
        List<SeriesPart> requiredGameIds,
        String iconUrl
) {}
