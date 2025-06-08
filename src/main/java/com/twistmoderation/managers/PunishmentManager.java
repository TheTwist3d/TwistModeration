package com.twistmoderation.managers;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.models.Punishment;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PunishmentManager {
    
    private final TwistModeration plugin;
    
    public PunishmentManager(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    public List<Punishment> getPlayerHistory(UUID playerUuid) {
        try {
            return plugin.getDatabaseManager().getPlayerHistory(playerUuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting player history: " + e.getMessage());
            return List.of();
        }
    }
    
    public int getWarningCount(UUID playerUuid) {
        try {
            return plugin.getDatabaseManager().getWarningCount(playerUuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting warning count: " + e.getMessage());
            return 0;
        }
    }
}