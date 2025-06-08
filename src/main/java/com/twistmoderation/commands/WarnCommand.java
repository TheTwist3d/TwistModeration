package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public WarnCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.warn")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /warn <player> <reason>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        plugin.getModerationManager().warnPlayer(target.getUniqueId(), target.getName(), reason, punisherName);
        
        String message = plugin.getConfigManager().getMessage("warn-success");
        message = MessageUtils.replacePlaceholders(message, target.getName(), reason);
        MessageUtils.sendPrefixedMessage(sender, message);
        
        return true;
    }
}