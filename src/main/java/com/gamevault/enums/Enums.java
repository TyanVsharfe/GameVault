package com.gamevault.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public class Enums {
    @Getter
    public enum CategoryIGDB {
        MAIN_GAME(0),
        DLC(1),
        EXPANSION(2),
        BUNDLE(3),
        STANDALONE_EXPANSION(4),
        MOD(5),
        EPISODE(6),
        SEASON(7),
        REMAKE(8),
        REMASTER(9),
        EXPANDED_GAME(10),
        PORT(11),
        FORK(12),
        PACK(13),
        UPDATE(14);

        private final int number;

        CategoryIGDB(int number) {
            this.number = number;
        }

        public static CategoryIGDB fromNumber(int number) {
            for (CategoryIGDB category : CategoryIGDB.values()) {
                if (category.getNumber() == number) {
                    return category;
                }
            }
            throw new IllegalArgumentException("No category found for number: " + number);
        }
    }

    @Getter
    public enum GameModesIGDB {
        SINGLE_PLAYER("single-player"),
        MULTIPLAYER("multiplayer"),
        CO_OPERATIVE("co-operative"),
        SPLIT_SCREEN("split-screen"),
        MMO("mmo"),
        BATTLE_ROYALE("battle-royale");

        private final String slug;

        GameModesIGDB(String slug) {
            this.slug = slug;
        }

        @JsonValue
        public String toJson() {
            return slug;
        }

        @JsonCreator
        public static GameModesIGDB fromJson(String value) {
            for (GameModesIGDB mode : values()) {
                if (mode.slug.equalsIgnoreCase(value)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("Unknown mode: " + value);
        }
    }

    public enum StatusIGDB {
        RELEASED,
        UNRELEASED,
        ALPHA,
        BETA,
        EARLY_ACCESS,
        OFFLINE,
        CANCELLED,
        RUMORED,
        DELISTED
    }

    public enum Status {
        COMPLETED("completed"),
        PLAYING("playing"),
        PLAYED("played"),
        PLANNED("planned"),
        ABANDONED("abandoned"),
        NONE("none");

        private final String slug;

        Status(String slug) {
            this.slug = slug;
        }

        @JsonValue
        public String toJson() {
            return slug;
        }

        @JsonCreator
        public static Status fromJson(String value) {
            for (Status status : values()) {
                if (status.slug.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid game status: " + value);
        }
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

    public enum Subscription {
        FREE,
        TRACKER
    }

    public enum AchievementCategory {
        TOTAL_GAMES_COMPLETED,
        GENRE_SPECIFIC,
        SERIES_COMPLETED,
        REVIEWS_WRITTEN
    }
}
