package com.gamevault.listener;

import com.gamevault.events.UserGameCompletedEvent;
import com.gamevault.service.achievement.AchievementProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class UserGameAchievementListener {
    private final AchievementProcessorService achievementProcessorService;

    public UserGameAchievementListener(AchievementProcessorService achievementProcessorService) {
        this.achievementProcessorService = achievementProcessorService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserGameCompleted(UserGameCompletedEvent event) {
        log.info("UserGameCompletedEvent received for user: {}, game: {}",
                event.user().getUsername(), event.userGame().getGame().getTitle());
        achievementProcessorService.processAchievementCompletion(event.user());
    }
}
