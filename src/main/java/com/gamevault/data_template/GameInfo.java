package com.gamevault.data_template;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameInfo {
    private int id;
    private int[] alternative_names; // Array of Alternative Name IDs
    private Enums.category category;
    private int collection; // The series the game belongs to
    private int cover; //
    private int[] dlcs;
    private Date first_release_date;
    private int franchise; // The main franchise
    private int[] game_engines; // The game engine id's used in this game
    private int[] keywords; // 	Associated keywords id's
    private String name;
    private int[] platforms;
    private String slug; // An url-safe, unique, lower-case version of the name
    // Пример "slug": "halo-3-odst"
    private Enums.status status;
    private String storyline;
    private String summary;
    private Double total_rating;
    private String getReleased() {
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy"); // Желаемый формат даты
        return targetFormat.format(this.first_release_date);
    }

    public int getCover() {
        return cover;
    }

    public String getSummary() {
        return summary;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return first_release_date;
    }
}
