package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public KickCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.kick")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /kick <player> [reason]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason provided";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        plugin.getModerationManager().kickPlayer(target.getUniqueId(), target.getName(), reason, punisherName);
        
        String message = plugin.getConfigManager().getMessage("kick-success");
        message = MessageUtils.replacePlaceholders(message, target.getName(), reason);
        MessageUtils.sendPrefixedMessage(sender, message);
        
        return true;
    }
}