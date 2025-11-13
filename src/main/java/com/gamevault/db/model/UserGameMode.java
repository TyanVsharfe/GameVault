package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "user_game_modes")
public class UserGameMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_id", nullable = false)
    @JsonBackReference
    private UserGame userGame;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.GameModesIGDB mode;

    @Setter
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.NONE;

    @Setter
    private Double userRating;

    public UserGameMode(UserGame userGame, Enums.GameModesIGDB mode) {
        this.userGame = userGame;
        this.mode = mode;
    }
}
