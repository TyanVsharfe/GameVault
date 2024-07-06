package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gamevault.form.NoteForm;
import jakarta.persistence.*;

@Entity
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", referencedColumnName = "igdbId")
    @JsonBackReference
    private Game game;

    public Note() {

    }

    public Note(NoteForm noteForm, Game game) {
        this.content = noteForm.content();
        this.game = game;
    }

    public Note(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
