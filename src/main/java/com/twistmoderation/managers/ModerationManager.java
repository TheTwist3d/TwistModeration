package com.twistmoderation.managers;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.models.Punishment;
import com.twistmoderation.models.PunishmentType;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class ModerationManager {
    
    private final TwistModeration plugin;
    
    public ModerationManager(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    public void mutePlayer(UUID playerUuid, String playerName, String reason, String punisherName, long duration) {
        try {
            // Add to database
            PunishmentType type = duration > 0 ? PunishmentType.TEMP_MUTE : PunishmentType.MUTE;
            Punishment punishment = new Punishment(playerUuid, playerName, type, reason, null, punisherName, System.currentTimeMillis(), duration);
            plugin.getDatabaseManager().addPunishment(punishment);
            plugin.getDatabaseManager().addActiveMute(playerUuid, playerName, reason, punisherName, duration);
            
            // Notify player if online
            Player player = Bukkit.getPlayer(playerUuid);
            if (player != null) {
                String message = plugin.getConfigManager().getMessage("you-are-muted");
                message = MessageUtils.replacePlaceholders(message, playerName, reason);
                MessageUtils.sendPrefixedMessage(player, message);
            }
            
            // Notify staff
            if (plugin.getConfigManager().areStaffNotificationsEnabled()) {
                String action = duration > 0 ? "TEMP-MUTED" : "MUTED";
                String notification = plugin.getConfigManager().getStaffNotificationFormat()
                    .replace("{action}", action)
                    .replace("{target}", playerName)
                    .replace("{reason}", reason);
                MessageUtils.broadcastToStaff(notification);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Error muting player: " + e.getMessage());
        }
    }
    
    public void unmutePlayer(UUID playerUuid, String playerName) {
        try {
            plugin.getDatabaseManager().removeActiveMute(playerUuid);
            
            // Notify staff
            if (plugin.getConfigManager().areStaffNotificationsEnabled()) {
                String notification = plugin.getConfigManager().getStaffNotificationFormat()
                    .replace("{action}", "UNMUTED")
                    .replace("{target}", playerName)
                    .replace("{reason}", "Manual unmute");
                MessageUtils.broadcastToStaff(notification);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Error unmuting player: " + e.getMessage());
        }
    }
    
    public void warnPlayer(UUID playerUuid, String playerName, String reason, String punisherName) {
        try {
            // Add warning to database
            Punishment punishment = new Punishment(playerUuid, playerName, PunishmentType.WARN, reason, null, punisherName, System.currentTimeMillis(), 0);
            plugin.getDatabaseManager().addPunishment(punishment);
            
            // Notify player if online
            Player player = Bukkit.getPlayer(playerUuid);
            if (player != null) {
                String message = plugin.getConfigManager().getMessage("you-have-been-warned");
                message = MessageUtils.replacePlaceholders(message, playerName, reason);
                MessageUtils.sendPrefixedMessage(player, message);
            }
            
            // Check for auto-punishments
            checkAutoPunishments(playerUuid, playerName);
            
            // Notify staff
            if (plugin.getConfigManager().areStaffNotificationsEnabled()) {
                String notification = plugin.getConfigManager().getStaffNotificationFormat()
                    .replace("{action}", "WARNED")
                    .replace("{target}", playerName)
                    .replace("{reason}", reason);
                MessageUtils.broadcastToStaff(notification);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Error warning player: " + e.getMessage());
        }
    }
    
    public void kickPlayer(UUID playerUuid, String playerName, String reason, String punisherName) {
        try {
            // Add kick to database
            Punishment punishment = new Punishment(playerUuid, playerName, PunishmentType.KICK, reason, null, punisherName, System.currentTimeMillis(), 0);
            plugin.getDatabaseManager().addPunishment(punishment);
            
            // Kick player if online
            Player player = Bukkit.getPlayer(playerUuid);
            if (player != null) {
                String kickMessage = plugin.getConfigManager().getMessage("you-have-been-kicked");
                kickMessage = MessageUtils.replacePlaceholders(kickMessage, playerName, reason);
                player.kick(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessage));
            }
            
            // Check for auto-ban
            checkAutoBan(playerUuid, playerName);
            
            // Notify staff
            if (plugin.getConfigManager().areStaffNotificationsEnabled()) {
                String notification = plugin.getConfigManager().getStaffNotificationFormat()
                    .replace("{action}", "KICKED")
                    .replace("{target}", playerName)
                    .replace("{reason}", reason);
                MessageUtils.broadcastToStaff(notification);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Error kicking player: " + e.getMessage());
        }
    }
    
    private void checkAutoPunishments(UUID playerUuid, String playerName) {
        try {
            int warnings = plugin.getDatabaseManager().getWarningCount(playerUuid);
            
            // Auto-mute check
            if (plugin.getConfigManager().isAutoMuteEnabled() && 
                warnings >= plugin.getConfigManager().getAutoMuteThreshold()) {
                
                long duration = plugin.getConfigManager().getAutoMuteDuration();
                mutePlayer(playerUuid, playerName, "Automatic mute - too many warnings", "TwistModeration", duration);
            }
            
            // Auto-kick check
            if (plugin.getConfigManager().isAutoKickEnabled() && 
                warnings >= plugin.getConfigManager().getAutoKickThreshold()) {
                
                kickPlayer(playerUuid, playerName, "Automatic kick - too many warnings", "TwistModeration");
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking auto-punishments: " + e.getMessage());
        }
    }
    
    private void checkAutoBan(UUID playerUuid, String playerName) {
        try {
            if (plugin.getConfigManager().isAutoBanEnabled()) {
                int kicks = plugin.getDatabaseManager().getKickCount(playerUuid);
                
                if (kicks >= plugin.getConfigManager().getAutoBanThreshold()) {
                    long duration = plugin.getConfigManager().getAutoBanDuration();
                    // Note: You would implement ban functionality here
                    // For now, we'll just log it
                    plugin.getLogger().info("Player " + playerName + " should be auto-banned (too many kicks)");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking auto-ban: " + e.getMessage());
        }
    }
    
    public boolean isPlayerMuted(UUID playerUuid) {
        try {
            return plugin.getDatabaseManager().isPlayerMuted(playerUuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking mute status: " + e.getMessage());
            return false;
        }
    }
    
    public String getMuteReason(UUID playerUuid) {
        try {
            return plugin.getDatabaseManager().getMuteReason(playerUuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting mute reason: " + e.getMessage());
            return "Unknown reason";
        }
    }
}