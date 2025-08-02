package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gamevault.form.NoteForm;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_id", referencedColumnName = "id")
    @JsonBackReference
    private UserGame userGame;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Note(NoteForm noteForm, UserGame userGame, User user) {
        this.title = noteForm.title();
        this.content = noteForm.content();
        this.userGame = userGame;
        this.user = user;
    }
}
