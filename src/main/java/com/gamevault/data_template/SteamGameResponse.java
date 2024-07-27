package com.gamevault.data_template;

import java.util.List;

public class SteamGameResponse {
    private int game_count;
    private List<SteamGame> games;

    public int getGame_count() {
        return game_count;
    }

    public void setGame_count(int game_count) {
        this.game_count = game_count;
    }

    public List<SteamGame> getGames() {
        return games;
    }

    public void setGames(List<SteamGame> games) {
        this.games = games;
    }
}
