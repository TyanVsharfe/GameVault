package com.gamevault.enums;

import lombok.Getter;

public class Enums {
    @Getter
    public enum categoryIGDB {
        main_game(0),
        dlc(1),
        expansion(2),
        bundle(3),
        standalone_expansion(4),
        mod(5),
        episode(6),
        season(7),
        remake(8),
        remaster(9),
        expanded_game(10),
        port(11),
        fork(12),
        pack(13),
        update(14);

        private final int number;

        categoryIGDB(int number) {
            this.number = number;
        }

        public static Enums.categoryIGDB fromNumber(int number) {
            for (Enums.categoryIGDB category : Enums.categoryIGDB.values()) {
                if (category.getNumber() == number) {
                    return category;
                }
            }
            throw new IllegalArgumentException("No category found for number: " + number);
        }
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
