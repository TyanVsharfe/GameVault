package com.gamevault.data_template;

public class Enums {
    public enum categoryIGDB {
        main_game,
        dlc_addon,
        expansion,
        bundle,
        standalone_expansion,
        mod,
        episode,
        season,
        remake,
        remaster,
        expanded_game,
        port,
        fork,
        pack,
        update
    }

    public enum statusIGDB {
        released,
        unreleased,
        alpha,
        beta,
        early_access,
        offline,
        cancelled,
        rumored,
        delisted
    }

    public enum status {
        Completed,
        Playing,
        Planned,
        Abandoned,
        None
    }

    public enum platforms {
        PC(6),

        PS1(7),
        PS2(8),
        PS3(9),
        PS4(48),
        PS5(167),

        XBOX(11),
        X360(12),
        XONE(49),
        SERIES_X(169);

        platforms(long i) {
        }
    }

    public enum subscription {
        Free,
        Tracker
    }

    public enum AchievementCategory {
        TOTAL_GAMES_COMPLETED,
        GENRE_SPECIFIC,
        SERIES_COMPLETED,
        REVIEWS_WRITTEN
    }

}
