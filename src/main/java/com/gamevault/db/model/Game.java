package com.gamevault.db.model;

import com.gamevault.form.GameForm;
import jakarta.persistence.*;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;
    private Long igdbId;
    private String title;
    private Double userRating;
    private String coverUrl;
    @Lob
    private byte[] userScreenshots;

    public Game() {

    }

    public Game(GameForm gameForm) {
        this.igdbId = gameForm.getId();
        System.out.println("get id " + gameForm.getId());
        this.title = gameForm.getTitle();
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
}
