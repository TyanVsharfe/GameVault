package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "USER_GAME_LIST_ITEMS")
public class UserGameListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name ="user_game_list_id", nullable = false)
    @JsonBackReference
    private UserGameList userGameList;

    @ManyToOne
    @JoinColumn(name ="game_id", nullable = false)
    private Game game;

    @Setter
    @Column(name = "item_order")
    private Integer order;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String note;

    public UserGameListItem(UserGameList userGameList, Game game, Integer order) {
        this.userGameList = userGameList;
        this.game = game;
        this.order = order;
    }
}
