package com.gamevault.service.integration.steam;

import com.gamevault.dto.input.SteamImportTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SteamImportProducer {
    private final KafkaTemplate<String, SteamImportTask> kafkaTemplate;

    @Value("${app.kafka.topics.steam-save}")
    private String steamSaveTopic;

    public SteamImportProducer(KafkaTemplate<String, SteamImportTask> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSteamImportTask(SteamImportTask steamImportTask) {
        log.info("{} games sent to Kafka for saving", steamImportTask.gameIds().size());
        kafkaTemplate.send(steamSaveTopic, steamImportTask);
    }
}
