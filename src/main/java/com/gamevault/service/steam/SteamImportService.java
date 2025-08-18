package com.gamevault.service.steam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gamevault.dto.input.steam.SteamGame;
import com.gamevault.db.model.User;
import com.gamevault.dto.output.igdb.IgdbGameDTO;
import com.gamevault.service.IgdbGameService;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SteamImportService {
    private final SteamWebApiService steamWebApiService;
    private final IgdbGameService igdbGameService;

    public SteamImportService(SteamWebApiService steamWebApiService, IgdbGameService igdbGameService) {
        this.steamWebApiService = steamWebApiService;
        this.igdbGameService = igdbGameService;
    }

    private String cleanGameTitle(String title) {
        if (title == null) return "";

        return title
                .replaceAll("[™®©]", "")
                .trim();
    }

    public List<IgdbGameDTO> importSteamGames(Long steamId, User user) throws UnirestException, JsonProcessingException {
        List<String> steamGamesTitles = steamWebApiService.getGamesTitles(steamId).stream().map(SteamGame::getName).toList();
        steamGamesTitles = steamGamesTitles.stream()
                .limit(200)
                .map(this::cleanGameTitle)
                .toList();
        log.info("Imported {} games from Steam for SteamID={}", steamGamesTitles.size(), steamId);

        return new ObjectMapper().readValue(igdbGameService.steamImportGamesIGDB(steamGamesTitles), new TypeReference<>() {});
    }
}
