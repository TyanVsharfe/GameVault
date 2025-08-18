package com.gamevault.dto.input.steam;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SteamGameResponse {
    private int game_count;
    private List<SteamGame> games;
}
