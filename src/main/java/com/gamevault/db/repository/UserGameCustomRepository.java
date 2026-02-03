package com.gamevault.db.repository;

import com.gamevault.db.model.UserGame;
import com.gamevault.dto.input.UserGamesFilterParams;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.GameListReference;
import com.gamevault.dto.output.enriched.UserModeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserGameCustomRepository {
    Optional<UserGameBaseData> getUserGameBaseData(Long igdbId, String username);
    List<UserGameBatchData> getUserGamesBaseDataBatch(String username, Set<Long> igdbIds);
    Page<UserGame> findGamesWithFilters(UserGamesFilterParams params, String username, Pageable pageable);
    Map<Long, List<GameListReference>> getGameListsMap(String username, Set<Long> igdbIds);
    List<UserModeDto> findUserModes(Long userGameId);
    Double calculateAverageRating(String username);
}
