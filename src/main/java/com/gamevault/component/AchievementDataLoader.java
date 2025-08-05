//package com.gamevault.component;
//
//import com.gamevault.data_template.Enums;
//import com.gamevault.db.model.Achievement;
//import com.gamevault.db.model.User;
//import com.gamevault.db.model.UserAchievement;
//import com.gamevault.db.repository.AchievementRepository;
//import com.gamevault.db.repository.UserAchievementRepository;
//import com.gamevault.db.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Configuration
//public class AchievementDataLoader {
//    private final AchievementRepository achievementRepository;
//    private final UserAchievementRepository userAchievementRepository;
//    private final UserRepository userRepository;
//
//    public AchievementDataLoader(AchievementRepository achievementRepository, UserAchievementRepository userAchievementRepository, UserRepository userRepository) {
//        this.achievementRepository = achievementRepository;
//        this.userAchievementRepository = userAchievementRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Bean
//    public CommandLineRunner loadAchievementData() {
//        return args -> {
//            List<Achievement> totalGamesAchievements = Arrays.asList(
//                    new Achievement("Начинающий игрок", "Пройдите 5 игр",
//                            Enums.AchievementCategory.TOTAL_GAMES_COMPLETED, 5, "/icons/beginner.png", 10),
//                    new Achievement("Продвинутый игрок", "Пройдите 15 игр",
//                            Enums.AchievementCategory.TOTAL_GAMES_COMPLETED, 10, "/icons/advanced.png", 25),
//                    new Achievement("Опытный геймер", "Пройдите 25 игр",
//                            Enums.AchievementCategory.TOTAL_GAMES_COMPLETED, 25, "/icons/experience.png", 50),
//                    new Achievement("Игровой мастер", "Пройдите 50 игр",
//                            Enums.AchievementCategory.TOTAL_GAMES_COMPLETED, 50, "/icons/master.png", 100),
//                    new Achievement("Игрок-эксперт", "Пройдите 75 игр",
//                            Enums.AchievementCategory.TOTAL_GAMES_COMPLETED, 75, "/icons/expert.png", 150),
//                    new Achievement("Игроман", "Пройдите 100 игр",
//                            Enums.AchievementCategory.TOTAL_GAMES_COMPLETED, 100, "/icons/true_gamer.png", 250)
//            );
//
//            List<Achievement> allAchievements = new ArrayList<>(totalGamesAchievements);
//
//            achievementRepository.saveAll(allAchievements);
//        };
//    }
//
//    @Bean
//    public CommandLineRunner initializeAchievementsForAllUsers() {
//        return args -> {
//            List<User> allUsers = (List<User>) userRepository.findAll();
//            List<Achievement> allAchievements = (List<Achievement>) achievementRepository.findAll();
//
//            for (User user : allUsers) {
//                if (userAchievementRepository.countByUser(user) == 0) {
//                    List<UserAchievement> userAchievements = allAchievements.stream()
//                            .map(achievement -> new UserAchievement(user, achievement))
//                            .collect(Collectors.toList());
//
//                    userAchievementRepository.saveAll(userAchievements);
//                }
//            }
//        };
//    }
//}
