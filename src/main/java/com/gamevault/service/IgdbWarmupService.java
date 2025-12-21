package com.gamevault.service;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Component
@Slf4j
public class IgdbWarmupService {

    private final IgdbGameService igdbGameService;

    public IgdbWarmupService(IgdbGameService igdbGameService) {
        this.igdbGameService = igdbGameService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        log.info("Warming up IGDB connection...");

        Mono.delay(Duration.ofSeconds(1))
                .then(igdbGameService.searchGames("test"))
                .timeout(Duration.ofSeconds(15))
                .retry(1)
                .doOnSuccess(result ->
                        log.info("✓ IGDB connection warmed up successfully ({} results in cache)",
                                result.size())
                )
                .doOnError(e ->
                        log.error("✗ IGDB warmup failed: {}. First real request may be slow.",
                                e.getMessage())
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
