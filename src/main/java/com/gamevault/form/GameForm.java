package com.gamevault.form;

import java.util.Optional;

public class GameForm {
    private final Long id;
    private final String title;
    private String coverUrl;

    public GameForm(Long id, String Title) {
        this.id = id;
        this.title = Title;
    }

/*    public GameForm(Long id, String Title, String coverUrl) {
        this.id = id;
        this.title = Title;
        this.coverUrl = coverUrl;
    }*/

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
