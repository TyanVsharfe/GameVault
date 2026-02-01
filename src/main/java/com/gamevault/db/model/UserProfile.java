package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gamevault.dto.input.steam.UserSteamSettings;
import com.gamevault.dto.input.steam.UserSteamSync;
import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class UserProfile {

    @Id
    private UUID userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Setter
    private String steamId;
    @Setter
    private Boolean steamSyncEnabled = false;
    @Setter
    @Enumerated(EnumType.STRING)
    private Enums.SteamSync syncFrequency = Enums.SteamSync.MONTHLY;
    private Instant lastSteamAsyncAt;

    @ElementCollection
    private final Set<Long> ignoredGameIds = new HashSet<>();

    public void updateUserProfile(UserSteamSettings steamSettings) {
        if (steamSettings.steamId() != null) {
            this.steamId = steamSettings.steamId();
        }
        if (steamSettings.steamSyncEnabled() != null) {
            this.steamSyncEnabled = steamSettings.steamSyncEnabled();
        }
    }

    public void updateUserProfile(UserSteamSync steamSettings) {
        if (steamSettings.ignoredGameIds() != null) {
            this.ignoredGameIds.addAll(steamSettings.ignoredGameIds());
        }
        if (steamSettings.steamSyncEnabled() != null) {
            this.steamSyncEnabled = steamSettings.steamSyncEnabled();
        }
    }

    public void steamSyncStart(UserSteamSync steamSettings) {
        this.steamSyncEnabled = true;
        if (steamSettings.ignoredGameIds() != null) {
            this.ignoredGameIds.addAll(steamSettings.ignoredGameIds());
        }
    }

    public void steamSettingsClear() {
        this.steamId = null;
        this.steamSyncEnabled = false;
        this.ignoredGameIds.clear();
    }
}
