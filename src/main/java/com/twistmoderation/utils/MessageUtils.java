package com.twistmoderation.utils;

import com.twistmoderation.TwistModeration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class MessageUtils {
    
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    
    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        
        Component component = SERIALIZER.deserialize(message);
        sender.sendMessage(component);
    }
    
    public static void sendPrefixedMessage(CommandSender sender, String message) {
        String prefix = TwistModeration.getInstance().getConfigManager().getPrefix();
        sendMessage(sender, prefix + message);
    }
    
    public static void sendConsole(String message) {
        sendMessage(Bukkit.getConsoleSender(), message);
    }
    
    public static void broadcastToStaff(String message) {
        String permission = TwistModeration.getInstance().getConfigManager().getStaffNotificationPermission();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                sendMessage(player, message);
            }
        }
        
        sendConsole(message);
    }
    
    public static String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hour" + (hours != 1 ? "s" : "");
        } else {
            long days = seconds / 86400;
            return days + " day" + (days != 1 ? "s" : "");
        }
    }
    
    public static long parseDuration(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        
        input = input.toLowerCase().trim();
        
        try {
            // Extract number and unit
            String numberPart = input.replaceAll("[^0-9]", "");
            String unitPart = input.replaceAll("[0-9]", "");
            
            if (numberPart.isEmpty()) {
                return 0;
            }
            
            long number = Long.parseLong(numberPart);
            
            switch (unitPart) {
                case "s":
                case "sec":
                case "second":
                case "seconds":
                    return number;
                case "m":
                case "min":
                case "minute":
                case "minutes":
                    return number * 60;
                case "h":
                case "hour":
                case "hours":
                    return number * 3600;
                case "d":
                case "day":
                case "days":
                    return number * 86400;
                case "w":
                case "week":
                case "weeks":
                    return number * 604800;
                default:
                    // If no unit specified, assume minutes
                    return number * 60;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static String replacePlaceholders(String message, String target, String reason) {
        return message
                .replace("{player}", target)
                .replace("{target}", target)
                .replace("{reason}", reason != null ? reason : "No reason provided");
    }
    
    public static String replacePlaceholders(String message, String target, String reason, String duration) {
        return replacePlaceholders(message, target, reason)
                .replace("{duration}", duration);
    }
}