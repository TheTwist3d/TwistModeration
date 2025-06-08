package com.twistmoderation.utils;

import com.twistmoderation.TwistModeration;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final TwistModeration plugin;
    private FileConfiguration config;
    
    public ConfigManager(TwistModeration plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // Chat Filter Settings
    public boolean isChatFilterEnabled() {
        return config.getBoolean("chat-filter.enabled", true);
    }
    
    public String getChatFilterAction() {
        return config.getString("chat-filter.action", "WARN");
    }
    
    public boolean shouldNotifyStaff() {
        return config.getBoolean("chat-filter.notify-staff", true);
    }
    
    public boolean shouldBlockMessage() {
        return config.getBoolean("chat-filter.block-message", true);
    }
    
    public String getBlockedMessage() {
        return config.getString("chat-filter.blocked-message", "&c&l[TwistModeration] &7Your message contains inappropriate content and has been blocked.");
    }
    
    // Auto Punishment Settings
    public boolean isAutoMuteEnabled() {
        return config.getBoolean("auto-punishments.auto-mute.enabled", true);
    }
    
    public int getAutoMuteThreshold() {
        return config.getInt("auto-punishments.auto-mute.warnings-threshold", 3);
    }
    
    public int getAutoMuteDuration() {
        return config.getInt("auto-punishments.auto-mute.duration", 300);
    }
    
    public boolean isAutoKickEnabled() {
        return config.getBoolean("auto-punishments.auto-kick.enabled", true);
    }
    
    public int getAutoKickThreshold() {
        return config.getInt("auto-punishments.auto-kick.warnings-threshold", 5);
    }
    
    public boolean isAutoBanEnabled() {
        return config.getBoolean("auto-punishments.auto-ban.enabled", true);
    }
    
    public int getAutoBanThreshold() {
        return config.getInt("auto-punishments.auto-ban.kicks-threshold", 3);
    }
    
    public int getAutoBanDuration() {
        return config.getInt("auto-punishments.auto-ban.duration", 86400);
    }
    
    // Messages
    public String getMessage(String key) {
        return config.getString("messages." + key, "&cMessage not found: " + key);
    }
    
    public String getPrefix() {
        return getMessage("prefix");
    }
    
    // Staff Notifications
    public boolean areStaffNotificationsEnabled() {
        return config.getBoolean("staff-notifications.enabled", true);
    }
    
    public String getStaffNotificationPermission() {
        return config.getString("staff-notifications.permission", "twistmoderation.admin");
    }
    
    public String getStaffNotificationFormat() {
        return config.getString("staff-notifications.format", "&e&l[STAFF] &r&7{action} &8» &f{target} &8(&7{reason}&8)");
    }
}