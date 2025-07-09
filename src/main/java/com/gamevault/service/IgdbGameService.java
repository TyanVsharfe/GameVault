package com.gamevault.service;

import com.gamevault.component.IgdbTokenManager;
import com.gamevault.data_template.SteamGameTitle;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                        + "search *\"" + searchGame + "*\";"
                        + "where category = (0,8,9) & "
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
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                        + "where id = " + stringBuilder + "); limit 50;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String gameIGDB(String gameId) throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, "
                        + "game_type, summary, genres.name, first_release_date, platforms.abbreviation,"
                        + "franchises.name, franchises.slug, franchises.games.name, franchises.games.cover.url, "
                        + "franchises.games.platforms.abbreviation, franchises.games.release_dates.y, "
                        + "involved_companies.company.name, involved_companies.developer, involved_companies.publisher; "
                        + " where id = " + gameId + "; sort franchises.games.release_dates.y desc;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String gameSeries(String seriesTitle) throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/franchises")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name, games, slug,"
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
                .body("fields *, game.name, game.category, game.cover.url, game.platforms.abbreviation, game.hypes; "
                        + " where date > " + actualDate + " & region = 8;"
                        + "sort date asc;"
                        + "limit 50;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    public String steamImportGamesIGDB(SteamGameTitle[] gameTitles) throws UnirestException {

        StringBuilder titlesString = new StringBuilder("(");
        Arrays.stream(gameTitles).forEach(title -> titlesString.append("\"").append(title.getName()).append("\"").append(","));
        titlesString.replace(titlesString.length() - 1, titlesString.length(), ")");
        System.out.println("Titles string " + titlesString);

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", apiClient.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                        + "where name = " + titlesString + ";"
                ).asJson();

        return jsonResponse.getBody().toString();
    }
}
