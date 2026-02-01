package com.gamevault.service.user;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserProfile;
import com.gamevault.db.repository.UserProfileRepository;
import com.gamevault.dto.input.steam.UserSteamSettings;
import com.gamevault.dto.input.steam.UserSteamSync;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getUserSettings(User user) {
        Optional<UserProfile> optionalProfile = userProfileRepository.findById(user.getId());
        UserProfile profile;
        profile = optionalProfile.orElseGet(UserProfile::new);
        return profile;
    }

    public UserProfile steamSettingsUpdate(UserSteamSettings steamSettings, User user) {
        Optional<UserProfile> optionalProfile = userProfileRepository.findById(user.getId());
        UserProfile profile;
        profile = optionalProfile.orElseGet(UserProfile::new);
        profile.updateUserProfile(steamSettings);

        UserProfile saved = userProfileRepository.save(profile);
        return saved;
    }

    public UserProfile steamSettingsDelete(User user) {
        Optional<UserProfile> optionalProfile = userProfileRepository.findById(user.getId());
        UserProfile profile;
        profile = optionalProfile.orElseGet(UserProfile::new);
        profile.steamSettingsClear();

        UserProfile saved = userProfileRepository.save(profile);
        return saved;
    }

    public String getIgnoredSteamGames(User user) {
        return "";
    }

    public String addIgnoredSteamGame(String gameId, User user) {
        return "";
    }

    public String deleteIgnoredSteamGame(String gameId, User user) {
        return "";
    }

    public UserSteamSync steamSyncStart(UserSteamSync steamSettings, User user) {
        Optional<UserProfile> optionalProfile = userProfileRepository.findById(user.getId());
        UserProfile profile;
        profile = optionalProfile.orElseGet(UserProfile::new);
        profile.steamSyncStart(steamSettings);

        UserProfile saved = userProfileRepository.save(profile);
        return new UserSteamSync(saved.getSteamSyncEnabled(), saved.getIgnoredGameIds(), saved.getSyncFrequency());
    }

    public UserSteamSettings steamSyncRemove(User user) {
        Optional<UserProfile> optionalProfile = userProfileRepository.findById(user.getId());
        UserProfile profile;
        profile = optionalProfile.orElseGet(UserProfile::new);
        profile.setSteamSyncEnabled(false);

        UserProfile saved = userProfileRepository.save(profile);
        return new UserSteamSettings(saved.getSteamId(), saved.getSteamSyncEnabled());
    }

    public UserSteamSync steamSyncUpdate(UserSteamSync steamSync) {
        return steamSync;
    }
}
