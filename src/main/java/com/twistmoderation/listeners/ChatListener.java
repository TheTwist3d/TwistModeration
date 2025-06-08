package com.twistmoderation.listeners;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.ChatFilter;
import com.twistmoderation.utils.MessageUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    
    private final TwistModeration plugin;
    
    public ChatListener(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is muted
        if (plugin.getModerationManager().isPlayerMuted(player.getUniqueId())) {
            event.setCancelled(true);
            String reason = plugin.getModerationManager().getMuteReason(player.getUniqueId());
            String message = plugin.getConfigManager().getMessage("you-are-muted");
            message = MessageUtils.replacePlaceholders(message, player.getName(), reason);
            MessageUtils.sendPrefixedMessage(player, message);
            return;
        }
        
        // Check if player has bypass permission
        if (player.hasPermission("twistmoderation.bypass")) {
            return;
        }
        
        // Check chat filter
        if (plugin.getConfigManager().isChatFilterEnabled()) {
            String messageText = PlainTextComponentSerializer.plainText().serialize(event.message());
            
            if (ChatFilter.containsInappropriateContent(messageText)) {
                // Block the message if configured
                if (plugin.getConfigManager().shouldBlockMessage()) {
                    event.setCancelled(true);
                    String blockedMessage = plugin.getConfigManager().getBlockedMessage();
                    MessageUtils.sendMessage(player, blockedMessage);
                }
                
                // Take action based on configuration
                String action = plugin.getConfigManager().getChatFilterAction();
                String reason = "Inappropriate language detected";
                
                switch (action.toUpperCase()) {
                    case "WARN":
                        plugin.getModerationManager().warnPlayer(
                            player.getUniqueId(), 
                            player.getName(), 
                            reason, 
                            "TwistModeration"
                        );
                        break;
                    case "MUTE":
                        plugin.getModerationManager().mutePlayer(
                            player.getUniqueId(), 
                            player.getName(), 
                            reason, 
                            "TwistModeration", 
                            0 // Permanent mute
                        );
                        break;
                    case "KICK":
                        plugin.getModerationManager().kickPlayer(
                            player.getUniqueId(), 
                            player.getName(), 
                            reason, 
                            "TwistModeration"
                        );
                        break;
                }
                
                // Notify staff if configured
                if (plugin.getConfigManager().shouldNotifyStaff()) {
                    String notification = "&c&l[FILTER] &r&7" + player.getName() + " &8» &f" + messageText;
                    MessageUtils.broadcastToStaff(notification);
                }
            }
        }
    }
}