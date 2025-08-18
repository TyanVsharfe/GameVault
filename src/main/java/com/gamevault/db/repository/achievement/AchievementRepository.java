package com.gamevault.db.repository.achievement;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.Achievement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findByCategory(Enums.AchievementCategory category);
}
