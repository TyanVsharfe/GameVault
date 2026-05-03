package com.gamevault.service.enriched;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.repository.UserGameListRepository;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.*;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.IgdbGameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrichedGameService {
    private final IgdbGameService igdbService;
    private final UserGameListRepository userGameListRepository;
    private final UserGameEnrichmentLoader userGameEnrichmentLoader;

    public EnrichedGameService(IgdbGameService igdbService, UserGameListRepository userGameListRepository, UserGameEnrichmentLoader userGameEnrichmentLoader) {
        this.igdbService = igdbService;
        this.userGameListRepository = userGameListRepository;
        this.userGameEnrichmentLoader = userGameEnrichmentLoader;
    }

    public EnrichedGameDto getGameWithUserData(Long igdbId, User user) {
        IgdbGameDto igdbGame = igdbService.getGame(igdbId);

        if (user == null) {
            return EnrichedGameDto.fromIgdb(igdbGame);
        }

        UserGameData userGameData = userGameEnrichmentLoader.loadUserGameData(user, igdbId);

        return EnrichedGameDto.fromIgdb(igdbGame, userGameData);
    }

    public EnrichedGameList getGameListWithUserData(UUID listId, User user) {
        UserGameList list =  userGameListRepository.findByIdWithItems(listId)
                .orElseThrow(() -> new EntityNotFoundException("Game list not found"));

        if (user == null) {
            return EnrichedGameList.fromUserGameList(list);
        }

        if (!list.isPublic()) {
            if (!list.isOwnedBy(user)) {
                throw new AccessDeniedException("You don't have permission to modify this list");
            }
        }

        Set<Long> igdbGameIds = list.getItems().stream().map(item -> item.getGame().getIgdbId()).collect(Collectors.toSet());
        Map<Long, UserGameBatchData> games = userGameEnrichmentLoader.loadUserGameDataBatch(user, igdbGameIds);

        return EnrichedGameList.fromUserGameList(list, games);
    }
}
