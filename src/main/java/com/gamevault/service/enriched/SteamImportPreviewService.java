package com.gamevault.service.enriched;

import com.gamevault.db.model.User;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.*;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.steam.SteamImportService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SteamImportPreviewService {

    private final SteamImportService steamImportService;
    private final UserGameEnrichmentLoader userGameEnrichmentLoader;

    public SteamImportPreviewService(SteamImportService steamImportService, UserGameEnrichmentLoader userGameEnrichmentLoader) {
        this.steamImportService = steamImportService;
        this.userGameEnrichmentLoader = userGameEnrichmentLoader;
    }

    public List<EnrichedGameSearchDto> importSteamGamesWithUserData(Long steamId, User user) {
        List<IgdbGameDto> importResult = steamImportService.importSteamGames(steamId, user);

        if (importResult == null || importResult.isEmpty())  {
            return List.of();
        }

        if (user == null) {
            List<EnrichedGameSearchDto> result = importResult.stream()
                    .map(EnrichedGameSearchDto::fromIgdb)
                    .toList();
            return result;
        }

        Set<Long> gameIds = importResult.stream()
                .map(IgdbGameDto::id)
                .collect(Collectors.toSet());

        Map<Long, UserGameBatchData> userGameDataMap = userGameEnrichmentLoader.loadUserGameDataBatch(user, gameIds);
        return importResult.stream().map
                (igdbGame -> EnrichedGameSearchDto
                        .fromIgdb(igdbGame, userGameDataMap.get(igdbGame.id()))
                ).toList();
    }
}