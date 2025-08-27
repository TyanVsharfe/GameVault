package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gamevault.db.model.achievement.Achievement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_achievements")
@Setter
@Getter
@NoArgsConstructor
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column
    private Instant achievedAt;

    @Column(nullable = false)
    private int currentProgress;

    @Column(nullable = false)
    private Boolean isCompleted;

    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.achievedAt = null;
        this.currentProgress = 0;
        this.isCompleted = false;
    }
}
