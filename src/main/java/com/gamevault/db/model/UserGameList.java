package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gamevault.dto.input.UserGameListForm;
import com.gamevault.dto.output.UserGameListOutput;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "USER_GAME_LISTS")
public class UserGameList {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Setter
    private String authorUsername;

    @OneToMany(mappedBy = "userGameList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGameListItem> items = new ArrayList<>();

    @Setter
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Setter
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Setter
    @Column(nullable = false)
    private boolean isPublic = false;

    public UserGameList(User author, UserGameListForm listForm) {
        this.author = author;
        this.authorUsername = author.getUsername();
        this.name = listForm.name();
        if (listForm.isPublic() != null) {
            this.isPublic = listForm.isPublic();
        }
        if (listForm.description() != null) {
            this.description = listForm.description();
        }

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        this.createdAt = offsetDateTime.toInstant();
    }

    public UserGameList(User author, UserGameList original) {
        this.author = author;
        this.authorUsername = author.getUsername();
        this.name = original.getName();
        this.isPublic = original.isPublic;
        if (original.description != null) {
            this.description = original.description;
        }
        this.items = new ArrayList<>();

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        this.createdAt = offsetDateTime.toInstant();
    }

    public UserGameList(User author, String name, List<UserGameListItem> games) {
        this.author = author;
        this.authorUsername = author.getUsername();
        this.name = name;
        this.items = games;
        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        this.createdAt = offsetDateTime.toInstant();
    }

    public UserGameListOutput toOutput(User currentUser) {
        return new UserGameListOutput(
                this.uuid,
                this.name,
                this.authorUsername,
                this.description,
                this.isPublic,
                this.items,
                this.isOwnedBy(currentUser)
        );
    }

    public void addGame(Game game, Integer order) {
        UserGameListItem item = new UserGameListItem(this, game, order);
        this.items.add(item);
        updateTimestamp();
    }

    public void removeGame(Game game) {
        this.items.removeIf(item -> item.getGame().equals(game));
        updateTimestamp();
    }

    public void updateTimestamp() {
        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        this.updatedAt = offsetDateTime.toInstant();
    }

    public boolean isOwnedBy(User user) {
        return this.author.equals(user);
    }
}
