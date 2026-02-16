package com.gamevault.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Enums {
    @Getter
    public enum IgdbGameType {
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
        private static final Map<Integer, IgdbGameType> BY_NUMBER;

        static {
            BY_NUMBER = Arrays.stream(values())
                    .collect(Collectors
                            .toUnmodifiableMap(
                                    IgdbGameType::getNumber,
                                    Function.identity())
                    );
        }

        IgdbGameType(int number) {
            this.number = number;
        }

        public static IgdbGameType fromNumber(Integer number) {
            IgdbGameType category = BY_NUMBER.get(number);
            if (category == null) {
                throw new IllegalArgumentException("Unknown IGDB category number: " + number);
            }
            return category;
        }
    }

    @Getter
    public enum NoteType {
        GENERAL("general"),
        GAMEPLAY("gameplay"),
        GUIDE("guide"),
        COMBAT("combat"),
        LORE("lore");

        private final String slug;
        private static final Map<String, NoteType> BY_SLUG;

        static {
            BY_SLUG = Arrays.stream(values())
                    .collect(Collectors
                            .toUnmodifiableMap(
                                    NoteType::getSlug,
                                    Function.identity())
                    );
        }

        NoteType(String slug) {
            this.slug = slug;
        }

        @JsonValue
        public String toJson() {
            return slug;
        }

        @JsonCreator
        public static NoteType fromJson(String value) {
            NoteType noteType = BY_SLUG.get(value.toLowerCase());
            if (noteType == null) {
                throw new IllegalArgumentException("Unknown note type: " + value);
            }
            return noteType;
        }
    }


    @Getter
    public enum GameModesIGDB {
        SINGLE_PLAYER("single-player"),
        MULTIPLAYER("multiplayer"),
        CO_OPERATIVE("co-operative"),
        SPLIT_SCREEN("split-screen"),
        MMO("massively-multiplayer-online-mmo"),
        BATTLE_ROYALE("battle-royale");

        private final String slug;
        private static final Map<String, GameModesIGDB> BY_SLUG;

        static {
            BY_SLUG = Arrays.stream(values())
                    .collect(Collectors
                            .toUnmodifiableMap(
                                    GameModesIGDB::getSlug,
                                    Function.identity())
                    );
        }

        GameModesIGDB(String slug) {
            this.slug = slug;
        }

        @JsonValue
        public String toJson() {
            return slug;
        }

        @JsonCreator
        public static GameModesIGDB fromJson(String value) {
            GameModesIGDB gameMode = BY_SLUG.get(value.toLowerCase());
            if (gameMode == null) {
                throw new IllegalArgumentException("Unknown mode: " + value);
            }
            return gameMode;
        }
    }

    public enum ReleaseStatus {
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

    @Getter
    public enum Status {
        COMPLETED("completed"),
        PLAYING("playing"),
        PLAYED("played"),
        PLANNED("planned"),
        ABANDONED("abandoned"),
        NONE("none");

        private final String slug;
        private static final Map<String, Status> BY_SLUG;

        static {
            BY_SLUG = Arrays.stream(values())
                    .collect(Collectors
                            .toUnmodifiableMap(
                                    Status::getSlug,
                                    Function.identity())
                    );
        }

        Status(String slug) {
            this.slug = slug;
        }

        @JsonValue
        public String toJson() {
            return slug;
        }

        @JsonCreator
        public static Status fromJson(String value) {
            Status status = BY_SLUG.get(value.toLowerCase());
            if (status == null) {
                throw new IllegalArgumentException("Invalid game status: " + value);
            }
            return status;
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

    public enum SteamSync {
        WEEKLY,
        MONTHLY
    }
}
