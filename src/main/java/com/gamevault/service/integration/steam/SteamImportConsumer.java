package com.gamevault.service.integration.steam;

import com.gamevault.dto.input.SteamImportTask;
import com.gamevault.service.UserGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SteamImportConsumer {
    private final UserGameService userGameService;

    public SteamImportConsumer(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    @KafkaListener(topics = "${app.kafka.topics.steam-save}", groupId = "steam-save", containerFactory = "steamSaveKafkaListenerContainerFactory")
    public void processImportTask(SteamImportTask steamImportTask) {
        try {
            log.info("Received SteamImportTask: {}", steamImportTask);

            if (steamImportTask == null) {
                log.error("Received null SteamImportTask");
                throw new IllegalArgumentException("SteamImportTask is null");
            }
            if (steamImportTask.userId() == null) {
                log.error("UserId is null in SteamImportTask: {}", steamImportTask);
                throw new IllegalArgumentException("UserId is null");
            }
            if (steamImportTask.gameIds() == null || steamImportTask.gameIds().isEmpty()) {
                log.warn("GameIds is null or empty in SteamImportTask: {}", steamImportTask);
                return;
            }

            log.info("Processing {} games for User with UUID: {}", steamImportTask.gameIds().size(), steamImportTask.userId());
            for (Long gameId : steamImportTask.gameIds()) {
                userGameService.add(gameId, steamImportTask.userId());
            }

            log.info("Saved {} games from Kafka task for User with UUID: {}", steamImportTask.gameIds().size(), steamImportTask.userId());
        } catch (Exception e) {
            log.error("Error when processing game imports: {}", e.getMessage(), e);
            throw e;
        }
    }
}
