package com.gamevault.service.enriched;

import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserGameCustomRepository;
import com.gamevault.db.repository.UserGameListRepository;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.GameListReference;
import com.gamevault.dto.output.enriched.UserGameData;
import com.gamevault.dto.output.enriched.UserModeDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserGameEnrichmentLoader {
    private final UserGameCustomRepository userGameCustomRepository;
    private final UserGameListRepository userGameListRepository;

    public UserGameEnrichmentLoader(UserGameCustomRepository userGameCustomRepository, UserGameListRepository userGameListRepository) {
        this.userGameCustomRepository = userGameCustomRepository;
        this.userGameListRepository = userGameListRepository;
    }

    public UserGameData loadUserGameData(User user, Long igdbGameId) {

        UserGameBaseData base = userGameCustomRepository
                .getUserGameBaseData(igdbGameId, user.getUsername())
                .orElse(null);

        if (base == null) {
            return null;
        }

        List<UserModeDto> modes = userGameCustomRepository.findUserModes(base.userGameId());

        List<GameListReference> lists = loadGameLists(igdbGameId, user);
        return UserGameData.fromUserGameBase(base, modes, lists);
    }

    public Map<Long, UserGameBatchData> loadUserGameDataBatch(User user, Set<Long> igdbIds) {
        List<UserGameBatchData> baseData = userGameCustomRepository.getUserGamesBaseDataBatch(user.getUsername(), igdbIds);

        if (baseData.isEmpty()) {
            return Map.of();
        }

        Map<Long, UserGameBatchData> gamesMap = baseData.stream()
                .collect(Collectors.toMap(
                        UserGameBatchData::getIgdbId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<Long, List<GameListReference>> listsMap = userGameCustomRepository.getGameListsMap(user.getUsername(), igdbIds);

        listsMap.forEach((igdbId, references) -> {
            UserGameBatchData data = gamesMap.get(igdbId);
            if (data != null) {
                data.setInLists(references);
            }
        });

        return gamesMap;
    }

    public List<GameListReference> loadGameLists(Long userGameId, User user) {
        return userGameListRepository.findListsByUserGameIdAndAuthorUsername(userGameId, user.getUsername());
    }
}
