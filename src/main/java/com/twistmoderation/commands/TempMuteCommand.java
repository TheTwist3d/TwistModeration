package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempMuteCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public TempMuteCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.tempmute")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /tempmute <player> <duration> [reason]");
            MessageUtils.sendPrefixedMessage(sender, "&7Duration examples: 5m, 1h, 2d, 1w");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        if (plugin.getModerationManager().isPlayerMuted(target.getUniqueId())) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-already-muted"));
            return true;
        }
        
        long duration = MessageUtils.parseDuration(args[1]);
        if (duration <= 0) {
            MessageUtils.sendPrefixedMessage(sender, "&cInvalid duration format! Examples: 5m, 1h, 2d, 1w");
            return true;
        }
        
        String reason = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No reason provided";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        plugin.getModerationManager().mutePlayer(target.getUniqueId(), target.getName(), reason, punisherName, duration);
        
        String message = plugin.getConfigManager().getMessage("tempmute-success");
        message = MessageUtils.replacePlaceholders(message, target.getName(), reason, MessageUtils.formatDuration(duration));
        MessageUtils.sendPrefixedMessage(sender, message);
        
        return true;
    }
}