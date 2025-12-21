package com.gamevault.service;

import com.gamevault.component.IgdbTokenManager;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.dto.output.igdb.Series;
import com.gamevault.exception.IgdbApiException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class IgdbGameService {
    private final IgdbTokenManager tokenManager;
    private final WebClient webClient;
    
    public IgdbGameService(IgdbTokenManager tokenManager, WebClient.Builder webClientBuilder) {
        this.tokenManager = tokenManager;

        ConnectionProvider connectionProvider = ConnectionProvider.builder("igdb-pool")
                .maxConnections(50)
                .pendingAcquireMaxCount(100)
                .maxIdleTime(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofMinutes(10))
                .evictInBackground(Duration.ofSeconds(30))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10))
                );

        this.webClient = webClientBuilder
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .baseUrl("https://api.igdb.com/v4")
                .defaultHeader("Client-ID", tokenManager.getClient_id())
                .defaultHeader("Connection", "keep-alive")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<List<IgdbGameDto>> searchGames(String search) {

        return webClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Authorization", "Bearer " + tokenManager.getAccess_token())
                .bodyValue("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating,"
                        + "game_type.id, game_type.type, game_modes.name, first_release_date, game_type,"
                        + "dlcs.name, dlcs.cover.url, dlcs.game_type.id, dlcs.game_type.type, dlcs.game_status.status, dlcs.game_modes.name, standalone_expansions,"
                        + "expansions.name, expansions.game_type.*, expansions.game_status.status, expansions.cover.url, expansions.game_modes.name;"
                        + "search *\"" + search + "*\";"
                        + "where game_type = (0,1,4,8,9) &"
                        //+ "platforms = (0,8) & "
                        + "version_parent = null;"
                        + "limit 200;")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("IGDB API error: status={}, body={}", response.statusCode(), errorBody);
                                    return Mono.error(new IgdbApiException(
                                            "IGDB API returned error: " + response.statusCode()
                                    ));
                                })
                )
                .bodyToFlux(IgdbGameDto.class)
                .collectList()
                .doOnSuccess(result -> log.debug("Successfully retrieved {} items from IGDB", result.size()))
                .doOnError(WebClientResponseException.class, e ->
                        log.error("WebClient error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString())
                );
    }

    public Mono<List<IgdbGameDto>> getGamesByIds(Iterable<Long> igdbIds) {

        StringBuilder stringBuilder = new StringBuilder("(");
        for (Long id: igdbIds) {
            stringBuilder.append(id).append(",");
        }
        stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));

        return webClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Authorization", "Bearer " + tokenManager.getAccess_token())
                .bodyValue("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation,"
                        + "aggregated_rating, first_release_date, game_type;"
                        + "where id = " + stringBuilder + "); limit 50;")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("IGDB API error: status={}, body={}", response.statusCode(), errorBody);
                                    return Mono.error(new IgdbApiException(
                                            "IGDB API returned error: " + response.statusCode()
                                    ));
                                })
                )
                .bodyToFlux(IgdbGameDto.class)
                .collectList()
                .doOnSuccess(result -> log.debug("Successfully retrieved {} items from IGDB", result.size()))
                .doOnError(WebClientResponseException.class, e ->
                        log.error("WebClient error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString())
                );
    }

    public Mono<IgdbGameDto> getGame(Long gameId) {

        return webClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Authorization", "Bearer " + tokenManager.getAccess_token())
                .bodyValue("fields name,cover.url, release_dates.y, "
                        + "game_type.id, game_type.type, parent_game.name, game_modes.name, game_modes.slug, summary, genres.name, first_release_date, platforms.abbreviation,"
                        + "collections.name, collections.slug, collections.games.name, collections.games.slug, collections.games.cover.url, collections.games.game_type.type,"
                        + "franchises.name, franchises.slug, franchises.games.name, franchises.games.cover.url,"
                        + "franchises.games.platforms.abbreviation, franchises.games.release_dates.y,"
                        + "involved_companies.company.name, involved_companies.developer, involved_companies.publisher,"
                        + "dlcs.name, dlcs.cover.url, dlcs.game_type.id, dlcs.game_type.type, dlcs.game_status.status, dlcs.summary, dlcs.game_modes.name, dlcs.game_modes.slug, standalone_expansions,"
                        + "expansions.name, expansions.game_type.type, expansions.game_status.status, expansions.cover.url, expansions.summary, expansions.game_modes.name, expansions.game_modes.slug;"
                        + "where id = " + gameId + "; sort franchises.games.release_dates.y desc;")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("IGDB API error: status={}, body={}", response.statusCode(), errorBody);
                                    return Mono.error(new IgdbApiException(
                                            "IGDB API returned error: " + response.statusCode()
                                    ));
                                })
                )
                .bodyToFlux(IgdbGameDto.class)
                .collectList()
                .flatMap(games -> {
                    if (games.isEmpty()) {
                        log.warn("Game with id {} not found", gameId);
                        return Mono.empty();
                    }
                    return Mono.just(games.get(0));
                })
                .doOnSuccess(result -> log.debug("Successfully retrieved game with id {}", result.id()))
                .doOnError(WebClientResponseException.class, e ->
                        log.error("WebClient error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString())
                );
    }

    public Mono<List<Series>> getGameSeries(String seriesTitle) {

        return webClient.post()
                .uri("https://api.igdb.com/v4/collections")
                .header("Authorization", "Bearer " + tokenManager.getAccess_token())
                .bodyValue("fields name, games, slug,"
                        + "games.game_type.type, games.parent_game,"
                        + "games.name, games.cover.url, games.platforms.abbreviation, games.first_release_date;"
                        + "where slug = \"" + seriesTitle + "\"; sort games.first_release_date desc;")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("IGDB API error: status={}, body={}", response.statusCode(), errorBody);
                                    return Mono.error(new IgdbApiException(
                                            "IGDB API returned error: " + response.statusCode()
                                    ));
                                })
                )
                .bodyToFlux(Series.class)
                .collectList()
                .doOnSuccess(result -> log.debug("Successfully retrieved {} items from IGDB", result.size()))
                .doOnError(WebClientResponseException.class, e ->
                        log.error("WebClient error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString())
                );
    }

    public Mono<List<com.fasterxml.jackson.databind.JsonNode>> getGamesReleaseDates() {

        long actualDate = System.currentTimeMillis()/1000;

        return webClient.post()
                .uri("https://api.igdb.com/v4/release_dates/")
                .header("Authorization", "Bearer " + tokenManager.getAccess_token())
                .bodyValue("fields *, game.name, game.game_type, game.category, game.cover.url, game.platforms.abbreviation, platform.abbreviation, game.hypes;"
                        + " where date > " + actualDate + " & release_region = 8;"
                        + "sort date asc;"
                        + "limit 50;")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("IGDB API error: status={}, body={}", response.statusCode(), errorBody);
                                    return Mono.error(new IgdbApiException(
                                            "IGDB API returned error: " + response.statusCode()
                                    ));
                                })
                )
                .bodyToFlux(com.fasterxml.jackson.databind.JsonNode.class)
                .collectList()
                .doOnSuccess(result -> log.debug("Successfully retrieved {} items from IGDB", result.size()))
                .doOnError(WebClientResponseException.class, e ->
                        log.error("WebClient error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString())
                );
    }

    public Mono<List<IgdbGameDto>> importSteamGames(List<String> steamGamesTitles) {

        StringBuilder titlesString = new StringBuilder("(");
        steamGamesTitles.stream().limit(200).forEach(title -> titlesString.append("\"").append(title).append("\"").append(","));

        titlesString.replace(titlesString.length() - 1, titlesString.length(), ")");

        return webClient.post()
                .uri("https://api.igdb.com/v4/games")
                .header("Authorization", "Bearer " + tokenManager.getAccess_token())
                .bodyValue("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                        + "where (name = " + titlesString + " | alternative_names.name = " + titlesString + ") & "
                        + "game_type = (0,1,4,5,8,9) & "
                        + "platforms.abbreviation = \"" + "PC" + "\";"
                        + "limit 300;")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("IGDB API error: status={}, body={}", response.statusCode(), errorBody);
                                    return Mono.error(new IgdbApiException(
                                            "IGDB API returned error: " + response.statusCode()
                                    ));
                                })
                )
                .bodyToFlux(IgdbGameDto.class)
                .collectList()
                .doOnSuccess(result -> log.debug("Successfully retrieved {} items from IGDB", result.size()))
                .doOnError(WebClientResponseException.class, e ->
                        log.error("WebClient error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString())
                );
    }
}
