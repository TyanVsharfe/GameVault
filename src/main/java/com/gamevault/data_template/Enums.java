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
}
