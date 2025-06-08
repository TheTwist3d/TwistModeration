package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    
    public MainCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("twistmoderation.admin")) {
                    MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                
                plugin.reloadConfiguration();
                MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("reload-success"));
                break;
                
            case "help":
                showHelp(sender);
                break;
                
            case "version":
                MessageUtils.sendMessage(sender, "&c&l[TwistModeration] &r&7Version: &e" + plugin.getDescription().getVersion());
                MessageUtils.sendMessage(sender, "&7Professional moderation plugin for Paper servers");
                break;
                
            default:
                showHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&c&l[TwistModeration] &r&7Help Menu");
        MessageUtils.sendMessage(sender, "&7&m----------------------------------------");
        MessageUtils.sendMessage(sender, "&e/mute <player> [reason] &8- &7Permanently mute a player");
        MessageUtils.sendMessage(sender, "&e/tempmute <player> <duration> [reason] &8- &7Temporarily mute a player");
        MessageUtils.sendMessage(sender, "&e/unmute <player> &8- &7Unmute a player");
        MessageUtils.sendMessage(sender, "&e/warn <player> <reason> &8- &7Warn a player");
        MessageUtils.sendMessage(sender, "&e/kick <player> [reason] &8- &7Kick a player");
        MessageUtils.sendMessage(sender, "&e/tempban <player> <duration> [reason] &8- &7Temporarily ban a player");
        MessageUtils.sendMessage(sender, "&e/unban <player> &8- &7Unban a player");
        MessageUtils.sendMessage(sender, "&e/clearchat [lines] &8- &7Clear the chat");
        MessageUtils.sendMessage(sender, "&e/history <player> &8- &7View punishment history");
        
        if (sender.hasPermission("twistmoderation.admin")) {
            MessageUtils.sendMessage(sender, "&e/twistmoderation reload &8- &7Reload configuration");
        }
        
        MessageUtils.sendMessage(sender, "&7&m----------------------------------------");
        MessageUtils.sendMessage(sender, "&7Duration examples: &e5m, 1h, 2d, 1w");
    }
}