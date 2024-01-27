package com.gamevault.db.model;

import com.gamevault.data_template.Enums;
import com.gamevault.form.GameForm;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;
    private Long igdbId;
    private String title;
    private Enums.status status;
    private Double userRating;
    private String coverUrl;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();
    @Lob
    private byte[] userScreenshots;

    public Game() {

    }

    public Game(GameForm gameForm) {
        this.igdbId = gameForm.id();
        this.title = gameForm.title();
        this.coverUrl = gameForm.coverUrl();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getIgdbId() {
        return igdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public Enums.status getStatus() {
        return status;
    }

    public void setStatus(Enums.status status) {
        this.status = status;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
