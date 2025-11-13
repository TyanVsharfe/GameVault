package com.gamevault.service;

import com.gamevault.component.IgdbTokenManager;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IgdbGameService {
    private final IgdbTokenManager apiClient;

    public IgdbGameService(IgdbTokenManager apiClient) {
        this.apiClient = apiClient;
    }

    public String gamesIGDB(String searchGame) throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating,"
                        + "game_type.id, game_type.type, game_modes.name, first_release_date, game_type,"
                        + "dlcs.name, dlcs.cover.url, dlcs.game_type.id, dlcs.game_type.type, dlcs.game_status.status, dlcs.game_modes.name, standalone_expansions,"
                        + "expansions.name, expansions.game_type.*, expansions.game_status.status, expansions.cover.url, expansions.game_modes.name;"
                        + "search *\"" + searchGame + "*\";"
                        + "where game_type = (0,1,4,8,9) &"
                        //+ "platforms = (0,8) & "
                        + "version_parent = null;"
                        + "limit 200;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String gamesIGDBids(Iterable<Long> igdbIds) throws UnirestException {

        StringBuilder stringBuilder = new StringBuilder("(");
        for (Long id: igdbIds) {
            stringBuilder.append(id).append(",");
        }
        stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation,"
                        + "aggregated_rating, first_release_date, game_type;"
                        + "where id = " + stringBuilder + "); limit 50;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String gameIGDB(String gameId) throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, "
                        + "game_type.id, game_type.type, parent_game, parent_game.name, game_modes.name, game_modes.slug, summary, genres.name, first_release_date, platforms.abbreviation,"
                        + "collections.name, collections.slug, collections.games.name, collections.games.slug, collections.games.cover.url, collections.games.game_type.type,"
                        + "franchises.name, franchises.slug, franchises.games.name, franchises.games.cover.url,"
                        + "franchises.games.platforms.abbreviation, franchises.games.release_dates.y,"
                        + "involved_companies.company.name, involved_companies.developer, involved_companies.publisher,"
                        + "dlcs.name, dlcs.cover.url, dlcs.game_type.id, dlcs.game_type.type, dlcs.game_status.status, dlcs.summary, dlcs.game_modes.name, dlcs.game_modes.slug, standalone_expansions,"
                        + "expansions.name, expansions.game_type.type, expansions.game_status.status, expansions.cover.url, expansions.summary, expansions.game_modes.name, expansions.game_modes.slug;"
                        + " where id = " + gameId + "; sort franchises.games.release_dates.y desc;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String gameSeries(String seriesTitle) throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/collections")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name, games, slug,"
                        + "games.game_type.type, games.parent_game,"
                        + "games.name, games.cover.url, games.platforms.abbreviation, games.first_release_date;"
                        + " where slug = \"" + seriesTitle + "\"; sort games.first_release_date desc;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String gamesReleaseDates() throws UnirestException {

        long actualDate = System.currentTimeMillis()/1000;

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/release_dates/")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields *, game.name, game.category, game.cover.url, game.platforms.abbreviation, game.hypes;"
                        + " where date > " + actualDate + " & region = 8;"
                        + "sort date asc;"
                        + "limit 50;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String steamImportGamesIGDB(List<String> steamGamesTitles) throws UnirestException {

        StringBuilder titlesString = new StringBuilder("(");
        steamGamesTitles.stream().limit(200).forEach(title -> titlesString.append("\"").append(title).append("\"").append(","));

        titlesString.replace(titlesString.length() - 1, titlesString.length(), ")");

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                        + "where (name = " + titlesString + " | alternative_names.name = " + titlesString + ")"
                        + " & platforms.abbreviation = \"" + "PC" + "\";"
                        + "limit 300;"
                ).asJson();

        return jsonResponse.getBody().toString();
    }
}
