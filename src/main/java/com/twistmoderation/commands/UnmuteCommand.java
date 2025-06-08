package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public UnmuteCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.unmute")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /unmute <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        if (!plugin.getModerationManager().isPlayerMuted(target.getUniqueId())) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-not-muted"));
            return true;
        }
        
        plugin.getModerationManager().unmutePlayer(target.getUniqueId(), target.getName());
        
        String message = plugin.getConfigManager().getMessage("unmute-success");
        message = MessageUtils.replacePlaceholders(message, target.getName(), "");
        MessageUtils.sendPrefixedMessage(sender, message);
        
        return true;
    }
}