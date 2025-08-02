package com.gamevault.db.model;

import com.gamevault.data_template.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Enums.AchievementCategory category;

    @Column(nullable = false)
    private int requiredCount;

    private String iconUrl;

    private int experiencePoints;

    public Achievement(String name, String description, Enums.AchievementCategory category,
                       int requiredCount, String iconUrl, int experiencePoints) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.requiredCount = requiredCount;
        this.iconUrl = iconUrl;
        this.experiencePoints = experiencePoints;
    }
}
