package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.*;
import com.gamevault.dto.input.update.UserGameModeUpdateForm;
import com.gamevault.enums.Enums;
import com.gamevault.dto.input.update.UserGameUpdateForm;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "user_games")
public class UserGame extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "parent_game_id")
    @JsonBackReference
    private UserGame parentGame;

    @OneToMany(mappedBy = "parentGame", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<UserGame> dlcs = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "userGame", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "userGame", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserGameMode> userModes = new ArrayList<>();

    @Setter
    @Column(columnDefinition = "TEXT")
    private String review;

    // TODO убрать is
    @Setter
    private boolean isFullyCompleted;

    @Setter
    private String platform;

    @Setter
    private Enums.Status status = Enums.Status.NONE;

    private Double userRating;

    // TODO убрать is
    @Column(nullable = false)
    private boolean isOverallRating = true;

    // TODO убрать is
    @Column(nullable = false)
    private boolean isOverallStatus = true;

    @Setter
    @Column(length = 512)
    private String customCoverUrl;

    public UserGame(User user, Game game) {
        this.user = user;
        this.customCoverUrl = game.getCoverUrl();
        this.isFullyCompleted = false;
        this.game = game;
        initializeUserModesFromGame();
    }

    public UserGame(User user, Game game, UserGame parent) {
        this.user = user;
        this.customCoverUrl = game.getCoverUrl();
        this.isFullyCompleted = false;
        this.game = game;
        initializeUserModesFromGame();
        this.parentGame = parent;
    }

    public void initializeUserModesFromGame() {
        if (this.game != null && this.game.getGameModes() != null) {
            for (Enums.GameModesIGDB gm : this.game.getGameModes()) {
                this.userModes.add(new UserGameMode(this, gm));
            }
        }
    }

    public void updateDto(UserGameUpdateForm dto) {
        if (dto.status() != null) {
            this.status = dto.status();
            this.isOverallStatus = true;
            setOverallModeStatus(this.status);
        }
        if (dto.resetUserRating() != null && dto.resetUserRating()) {
            this.userRating = null;
        }
        else if (dto.userRating() != null) {
            this.userRating = dto.userRating();
            this.isOverallRating = true;
            setOverallModeRating(dto.userRating());
            clearModeRating();
        }
        if (dto.platform() != null) {
            this.platform = dto.platform();
        }
        if (dto.review() != null) {
            this.review = dto.review();
        }
        if (dto.isFullyCompleted() != null) {
            this.isFullyCompleted = dto.isFullyCompleted();
        }
    }

    private void setOverallModeStatus(Enums.Status status) {
        for (UserGameMode m : userModes) {
            if (m.getStatus() != null) {
                m.setStatus(status);
            }
        }
    }

    private void setOverallModeRating(Double userRating) {
        for (UserGameMode m : userModes) {
            m.setUserRating(userRating);
        }
    }

    private void clearModeRating() {
        for (UserGameMode m : userModes) {
            if (m.getUserRating() != null) {
                m.setUserRating(null);
            }
        }
    }

    public void updateMode(Enums.GameModesIGDB mode, UserGameModeUpdateForm dto) {
        if (dto.status() != null) {
            setModeStatus(mode, dto.status());
            this.isOverallStatus = false;
            this.status = Enums.Status.NONE;
        }
        if (dto.userRating() != null) {
            setModeRating(mode, dto.userRating());
            this.isOverallRating = false;
        }
    }

    private void setModeStatus(Enums.GameModesIGDB mode, Enums.Status status) {
        if (this.getUserModes().size() > 1) {
            for (UserGameMode m : userModes) {
                if (m.getMode() == mode) {
                    m.setStatus(status);
                    return;
                }
            }
        }
    }

    public void setModeRating(Enums.GameModesIGDB mode, Double rating) {
        for (UserGameMode m : userModes) {
            if (m.getMode() == mode) {
                m.setUserRating(rating);
                computeOverallRating();
                return;
            }
        }
    }

    private void computeOverallRating() {
        List<Double> ratings = new ArrayList<>();
        for (UserGameMode m : userModes) {
            if (m.getUserRating() != null) {
                ratings.add(m.getUserRating());
            }
        }
        if (ratings.isEmpty()) return;
        Double overallRating = ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        setOverallRating(overallRating);
    }

    public void setOverallRating(Double rating) {
        this.userRating = rating;
        this.isOverallRating = true;
    }
}
