package com.gamevault.data_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamGame {
    private Long appid;
    private String name;
    private Long playtime_forever;
    private String img_icon_url;
    private boolean has_community_visible_stats;
    private Long playtime_windows_forever;
    private Long playtime_mac_forever;
    private Long playtime_linux_forever;
    private Long playtime_deck_forever;
    private Long rtime_last_played;
    private Long playtime_disconnected;
}
