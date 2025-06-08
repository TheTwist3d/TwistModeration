package com.twistmoderation.commands;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.models.Punishment;
import com.twistmoderation.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryCommand implements CommandExecutor {
    
    private final TwistModeration plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    
    public HistoryCommand(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("twistmoderation.history")) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            MessageUtils.sendPrefixedMessage(sender, "&cUsage: /history <player>");
            return true;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            MessageUtils.sendPrefixedMessage(sender, plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        List<Punishment> history = plugin.getPunishmentManager().getPlayerHistory(target.getUniqueId());
        
        if (history.isEmpty()) {
            MessageUtils.sendPrefixedMessage(sender, "&7No punishment history found for &e" + target.getName());
            return true;
        }
        
        MessageUtils.sendMessage(sender, "&c&l[TwistModeration] &r&7Punishment History for &e" + target.getName());
        MessageUtils.sendMessage(sender, "&7&m----------------------------------------");
        
        for (int i = 0; i < Math.min(history.size(), 10); i++) {
            Punishment punishment = history.get(i);
            String date = dateFormat.format(new Date(punishment.getTimestamp()));
            String status = punishment.isActive() ? "&a[ACTIVE]" : "&7[INACTIVE]";
            String duration = punishment.isPermanent() ? "Permanent" : MessageUtils.formatDuration(punishment.getDuration());
            
            MessageUtils.sendMessage(sender, status + " &f" + punishment.getType().name() + 
                " &8| &7" + date + " &8| &f" + punishment.getReason());
            
            if (!punishment.isPermanent()) {
                MessageUtils.sendMessage(sender, "  &8└ &7Duration: &f" + duration + " &8| &7By: &f" + punishment.getPunisherName());
            } else {
                MessageUtils.sendMessage(sender, "  &8└ &7By: &f" + punishment.getPunisherName());
            }
        }
        
        if (history.size() > 10) {
            MessageUtils.sendMessage(sender, "&7... and " + (history.size() - 10) + " more entries");
        }
        
        MessageUtils.sendMessage(sender, "&7&m----------------------------------------");
        MessageUtils.sendMessage(sender, "&7Total punishments: &e" + history.size());
        
        return true;
    }
}