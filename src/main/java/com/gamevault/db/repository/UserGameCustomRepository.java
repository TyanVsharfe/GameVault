package com.gamevault.db.repository;

import com.gamevault.db.model.UserGame;
import com.gamevault.dto.input.UserGamesFilterParams;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.UserModeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserGameCustomRepository {
    Optional<UserGameBaseData> getUserGameBaseData(Long igdbId, String username);
    List<UserModeDto> findUserModes(Long userGameId);
    Page<UserGame> findGamesWithFilters(UserGamesFilterParams params, String username, Pageable pageable);
    List<UserGameBatchData> getUserGamesBaseDataBatch(String username, Set<Long> igdbIds);
    Double calculateAverageRating(String username);
}
