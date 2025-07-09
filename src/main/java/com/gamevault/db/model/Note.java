package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gamevault.form.NoteForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_id", referencedColumnName = "id")
    @JsonBackReference
    private UserGame userGame;

    public Note(NoteForm noteForm, UserGame userGame) {
        this.content = noteForm.content();
        this.userGame = userGame;
    }

    public Note(String content) {
        this.content = content;
    }

}
