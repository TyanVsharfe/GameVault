package com.gamevault.form;

public class GameForm {
    private final Long id;
    private final String Title;

    public GameForm(Long id, String Title) {
        this.id = id;
        this.Title = Title;
    }

    public String getTitle() {
        return Title;
    }

    public Long getId() {
        return id;
    }
}
