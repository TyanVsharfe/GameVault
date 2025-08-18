package com.gamevault.db.repository.achievement;

import com.gamevault.db.model.AchievementTranslation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementTranslationRepository extends CrudRepository<AchievementTranslation, Long> {
}
