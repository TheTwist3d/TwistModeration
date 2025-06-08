package com.twistmoderation;

import com.twistmoderation.commands.*;
import com.twistmoderation.database.DatabaseManager;
import com.twistmoderation.listeners.ChatListener;
import com.twistmoderation.listeners.PlayerListener;
import com.twistmoderation.managers.ModerationManager;
import com.twistmoderation.managers.PunishmentManager;
import com.twistmoderation.utils.ConfigManager;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class TwistModeration extends JavaPlugin {
    
    private static TwistModeration instance;
    private DatabaseManager databaseManager;
    private ModerationManager moderationManager;
    private PunishmentManager punishmentManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Initialize database
        try {
            databaseManager = new DatabaseManager(this);
            databaseManager.initialize();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers
        moderationManager = new ModerationManager(this);
        punishmentManager = new PunishmentManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Startup message
        getLogger().info("TwistModeration v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info("Professional moderation system loaded successfully.");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("TwistModeration has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("tempmute").setExecutor(new TempMuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("history").setExecutor(new HistoryCommand(this));
        getCommand("twistmoderation").setExecutor(new MainCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    public void reloadConfiguration() {
        reloadConfig();
        configManager.reload();
        MessageUtils.sendConsole("&aTwistModeration configuration reloaded!");
    }
    
    // Getters
    public static TwistModeration getInstance() {
        return instance;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public ModerationManager getModerationManager() {
        return moderationManager;
    }
    
    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}