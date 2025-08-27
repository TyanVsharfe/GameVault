package com.gamevault.db.model.achievement;

import com.gamevault.dto.input.achievement.AchievementTranslationForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "achievement_translations")
@Getter
@Setter
@NoArgsConstructor
public class AchievementTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;

    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    public AchievementTranslation(String language, String name, String description, Achievement achievement) {
        this.language = language;
        this.name = name;
        this.description = description;
        this.achievement = achievement;
    }

    public AchievementTranslation(AchievementTranslationForm translation, Achievement achievement) {
        this.language = translation.language();
        this.name = translation.name();
        this.description = translation.description();
        this.achievement = achievement;
    }
}
