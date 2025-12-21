package com.gamevault.db.repository.achievement;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends CrudRepository<UserAchievement, Long> {
    long countByUser(User user);
    List<UserAchievement> findByUserId(UUID userId);
    List<UserAchievement> findUserAchievementsByUser_Id(UUID userId);

    List<UserAchievement> findByUserIdAndIsCompleted(UUID userId, Boolean isCompleted);

    Optional<UserAchievement> findByUserIdAndAchievementId(UUID userId, Long achievementId);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.username = :username AND ua.isCompleted = true")
    Integer countCompletedAchievements(String username);
}
