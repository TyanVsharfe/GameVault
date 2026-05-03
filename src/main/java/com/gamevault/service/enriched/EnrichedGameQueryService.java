package com.gamevault.service.enriched;

import com.gamevault.db.model.User;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.EnrichedGameSearchDto;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.IgdbGameService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnrichedGameQueryService {
    private final IgdbGameService igdbService;
    private final UserGameEnrichmentLoader userGameEnrichmentLoader;

    public EnrichedGameQueryService(IgdbGameService igdbService, UserGameEnrichmentLoader userGameEnrichmentLoader) {
        this.igdbService = igdbService;
        this.userGameEnrichmentLoader = userGameEnrichmentLoader;
    }

    public List<EnrichedGameSearchDto> searchGamesWithUserData(String query, User user) {
        List<IgdbGameDto> searchResults = igdbService.searchGames(query);
        if (searchResults == null || searchResults.isEmpty())  {
            return List.of();
        }

        if (user == null) {
            return searchResults.stream()
                    .map(EnrichedGameSearchDto::fromIgdb)
                    .toList();
        }

        Set<Long> gameIds = searchResults.stream()
                .map(IgdbGameDto::id)
                .collect(Collectors.toSet());

        Map<Long, UserGameBatchData> userGameDataMap = userGameEnrichmentLoader.loadUserGameDataBatch(user, gameIds);
        return searchResults.stream().map
                (igdbGame -> EnrichedGameSearchDto
                        .fromIgdb(igdbGame, userGameDataMap.get(igdbGame.id()))
                ).toList();
    }
}
