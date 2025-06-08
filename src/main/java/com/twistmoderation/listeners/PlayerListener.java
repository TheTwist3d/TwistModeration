package com.twistmoderation.listeners;

import com.twistmoderation.TwistModeration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    
    private final TwistModeration plugin;
    
    public PlayerListener(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check for expired temporary punishments when player joins
        // This is a good place to clean up expired mutes/bans
        plugin.getModerationManager().isPlayerMuted(event.getPlayer().getUniqueId());
    }
}