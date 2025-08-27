package com.gamevault.db.model.achievement;

import com.gamevault.enums.Enums;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("COUNT")
@Getter
@Setter
@NoArgsConstructor
public class CountAchievement extends Achievement {
    
    private int requiredCount;

    public CountAchievement(Enums.AchievementCategory category,
                            int requiredCount, String iconUrl, int experiencePoints) {
        setCategory(category);
        this.requiredCount = requiredCount;
        setIconUrl(iconUrl);
        setExperiencePoints(experiencePoints);
    }
}
