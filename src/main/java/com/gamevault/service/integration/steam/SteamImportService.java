package com.gamevault.service.integration.steam;

import com.gamevault.dto.input.steam.SteamGame;
import com.gamevault.db.model.User;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.IgdbGameService;
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

    public List<IgdbGameDto> importSteamGames(Long steamId, User user) {
        List<String> steamGamesTitles = steamWebApiService.getGamesTitles(steamId).stream().map(SteamGame::getName).toList();
        steamGamesTitles = steamGamesTitles.stream()
                .limit(200)
                .map(this::cleanGameTitle)
                .toList();
        log.info("Imported {} games from Steam for SteamID={}", steamGamesTitles.size(), steamId);

        return igdbGameService.importSteamGames(steamGamesTitles);
    }
}
