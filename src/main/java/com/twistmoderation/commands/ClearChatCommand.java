package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public ClearChatCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.clearchat")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        int lines = 100; // Default lines to clear
        
        if (args.length > 0) {
            try {
                lines = Integer.parseInt(args[0]);
                if (lines < 1 || lines > 500) {
                    MessageUtils.sendPrefixedMessage(sender, "&cLines must be between 1 and 500!");
                    return true;
                }
            } catch (NumberFormatException e) {
                MessageUtils.sendPrefixedMessage(sender, "&cInvalid number format!");
                return true;
            }
        }
        
        // Clear chat by sending empty lines
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("twistmoderation.bypass")) {
                for (int i = 0; i < lines; i++) {
                    player.sendMessage("");
                }
            }
        }
        
        // Send clear message
        String clearMessage = plugin.getConfigManager().getMessage("clearchat-success");
        clearMessage = MessageUtils.replacePlaceholders(clearMessage, sender.getName(), "");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            MessageUtils.sendPrefixedMessage(player, clearMessage);
        }
        
        return true;
    }
}