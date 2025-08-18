package com.gamevault.db.model;

import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Enums.AchievementCategory category;

    @Column(nullable = false)
    private int requiredCount;

    private String iconUrl;

    private int experiencePoints;

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AchievementTranslation> translations = new ArrayList<>();

    public Achievement(Enums.AchievementCategory category,
                       int requiredCount, String iconUrl, int experiencePoints) {
        this.category = category;
        this.requiredCount = requiredCount;
        this.iconUrl = iconUrl;
        this.experiencePoints = experiencePoints;
    }
}
