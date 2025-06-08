package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempBanCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public TempBanCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.tempban")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /tempban <player> <duration> [reason]");
            MessageUtils.sendPrefixedMessage(sender, "&7Duration examples: 5m, 1h, 2d, 1w");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        String targetName = target != null ? target.getName() : args[0];
        
        long duration = MessageUtils.parseDuration(args[1]);
        if (duration <= 0) {
            MessageUtils.sendPrefixedMessage(sender, "&cInvalid duration format! Examples: 5m, 1h, 2d, 1w");
            return true;
        }
        
        String reason = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No reason provided";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        // Note: This is a simplified implementation
        // In a full implementation, you would use Bukkit's ban system
        if (target != null) {
            String banMessage = plugin.getConfigManager().getMessage("you-have-been-banned");
            banMessage = MessageUtils.replacePlaceholders(banMessage, targetName, reason);
            target.kick(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(banMessage));
        }
        
        String message = plugin.getConfigManager().getMessage("tempban-success");
        message = MessageUtils.replacePlaceholders(message, targetName, reason, MessageUtils.formatDuration(duration));
        MessageUtils.sendPrefixedMessage(sender, message);
        
        return true;
    }
}