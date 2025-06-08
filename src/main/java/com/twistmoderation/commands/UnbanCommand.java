package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public UnbanCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.unban")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /unban <player>");
            return true;
        }
        
        String targetName = args[0];
        
        // Note: This is a simplified implementation
        // In a full implementation, you would use Bukkit's ban system
        try {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(targetName);
            
            String message = plugin.getConfigManager().getMessage("unban-success");
            message = MessageUtils.replacePlaceholders(message, targetName, "");
            MessageUtils.sendPrefixedMessage(sender, message);
        } catch (Exception e) {
            MessageUtils.sendPrefixedMessage(sender, "&cError unbanning player: " + e.getMessage());
        }
        
        return true;
    }
}