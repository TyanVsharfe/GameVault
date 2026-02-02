package com.gamevault.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.dto.output.igdb.Series;
import com.gamevault.http.igdb.interceptor.IgdbHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IgdbGameService {
    private final IgdbHttpClient igdbHttpClient;
    private final ObjectMapper objectMapper;
    
    public IgdbGameService(IgdbHttpClient igdbHttpClient, ObjectMapper objectMapper) {
        this.igdbHttpClient = igdbHttpClient;
        this.objectMapper = objectMapper;
    }

    public List<IgdbGameDto> searchGames(String search) {
        String body = "fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating,"
                        + "game_type.id, game_type.type, game_modes.name, first_release_date, game_type,"
                        + "dlcs.name, dlcs.cover.url, dlcs.game_type.id, dlcs.game_type.type, dlcs.game_status.status, dlcs.game_modes.name, standalone_expansions,"
                        + "expansions.name, expansions.game_type.*, expansions.game_status.status, expansions.cover.url, expansions.game_modes.name;"
                        + "search *\"" + search + "*\";"
                        + "where game_type = (0,1,4,8,9) & version_parent = null;"
                        + "limit 200;";

        return executeRequest("games", body, new TypeReference<>() {});
    }

    public List<JsonNode> getGamesByIds(List<Long> igdbIds) {
        String idsString = igdbIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String body = "fields name,cover.url, release_dates.y, platforms, platforms.abbreviation,"
                        + "aggregated_rating, first_release_date, game_type;"
                        + "where id = (" + idsString + "); limit 50; sort id asc;";

        return executeRequest("games", body, new TypeReference<>() {});
    }

    public Map<Long, JsonNode> getGamesByIdsBatch(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        List<JsonNode> games = getGamesByIds(new ArrayList<>(ids));

        return games.stream()
                .collect(Collectors.toMap(
                        g -> g.get("id").asLong(),
                        Function.identity()
                ));
    }

    public IgdbGameDto getGame(Long gameId) {
        String body = "fields name,cover.url, release_dates.y, "
                + "game_type.id, game_type.type, parent_game.name, game_modes.name, game_modes.slug, summary, genres.name, first_release_date, platforms.abbreviation,"
                + "collections.name, collections.slug, collections.games.name, collections.games.slug, collections.games.cover.url, collections.games.game_type.type,"
//                + "franchises.name, franchises.slug, franchises.games.name, franchises.games.cover.url,"
//                + "franchises.games.platforms.abbreviation, franchises.games.release_dates.y,"
                + "involved_companies.company.name, involved_companies.company.slug, involved_companies.developer, involved_companies.publisher,"
                + "dlcs.name, dlcs.cover.url, dlcs.game_type.id, dlcs.game_type.type, dlcs.game_status.status, dlcs.summary, dlcs.game_modes.name, dlcs.game_modes.slug, standalone_expansions,"
                + "expansions.name, expansions.game_type.type, expansions.game_status.status, expansions.cover.url, expansions.summary, expansions.game_modes.name, expansions.game_modes.slug;"
                + "where id = " + gameId + "; sort franchises.games.release_dates.y desc;";

        List<IgdbGameDto> game = executeRequest("games", body, new TypeReference<>() {});
        return game.get(0);
    }

    public List<Series> getGameSeries(String series) {
        String body = "fields name, games, slug,"
                + "games.game_type.type, games.parent_game.name,"
                + "games.name, games.cover.url, games.platforms.abbreviation, games.first_release_date;"
                + "where slug = \"" + series + "\"; sort games.first_release_date desc;";

        return executeRequest("collections", body, new TypeReference<>() {});
    }

    public List<JsonNode> getGamesReleaseDates() {
        long actualDate = System.currentTimeMillis() / 1000;

        String body = "fields *, game.name, game.game_type, game.category, game.cover.url, game.platforms.abbreviation, platform.abbreviation, game.hypes;"
                + " where date > " + actualDate + " & release_region = 8;"
                + "sort date asc;"
                + "limit 50;";

        return executeRequest("release_dates", body, new TypeReference<>() {});
    }

    public List<IgdbGameDto> importSteamGames(List<String> steamGamesTitles) {

        StringBuilder titlesString = new StringBuilder("(");
        steamGamesTitles.stream().limit(200).forEach(title -> titlesString.append("\"").append(title).append("\"").append(","));

        titlesString.replace(titlesString.length() - 1, titlesString.length(), ")");

        String body = "fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                + "where (name = " + titlesString + " | alternative_names.name = " + titlesString + ") & "
                + "game_type = (0,1,4,5,8,9) & "
                + "platforms.abbreviation = \"" + "PC" + "\";"
                + "limit 300;";

        return executeRequest("games", body, new TypeReference<>() {});
    }

    public List<JsonNode> getGameCompany(String company) {
        String body = "fields *, logo.url,"
                + "developed.name, developed.game_type.type, developed.cover.url;"
                + "where slug = \"" + company + "\"; sort games.first_release_date desc;";

        return executeRequest("companies", body, new TypeReference<>() {});
    }

    private <T> T executeRequest(String endpoint, String body, TypeReference<T> typeReference) {
        T igdbObject;
        try {
            String json = igdbHttpClient.post(endpoint, body);
            igdbObject = objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return igdbObject;
    }
}
