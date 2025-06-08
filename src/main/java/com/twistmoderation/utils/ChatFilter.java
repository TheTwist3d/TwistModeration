package com.twistmoderation.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatFilter {
    
    private static final Set<String> INAPPROPRIATE_WORDS = new HashSet<>(Arrays.asList(
        // Basic inappropriate words (you can expand this list)
        "damn", "hell", "crap", "stupid", "idiot", "moron", "dumb", "loser",
        "noob", "scrub", "trash", "garbage", "suck", "sucks", "hate", "kill",
        "die", "death", "murder", "suicide", "kys", "cancer", "aids", "gay",
        "retard", "retarded", "autistic", "autism", "nazi", "hitler", "jew",
        "nigger", "nigga", "faggot", "fag", "bitch", "whore", "slut", "hoe",
        "thot", "simp", "incel", "virgin", "porn", "sex", "sexy", "nude",
        "naked", "boobs", "tits", "ass", "butt", "penis", "dick", "cock",
        "pussy", "vagina", "fuck", "fucking", "fucked", "fucker", "shit",
        "shitty", "bullshit", "piss", "pissed", "bastard", "asshole"
    ));
    
    private static final Set<Pattern> INAPPROPRIATE_PATTERNS = new HashSet<>(Arrays.asList(
        Pattern.compile("(?i).*f+u+c+k+.*"),
        Pattern.compile("(?i).*s+h+i+t+.*"),
        Pattern.compile("(?i).*b+i+t+c+h+.*"),
        Pattern.compile("(?i).*d+a+m+n+.*"),
        Pattern.compile("(?i).*a+s+s+.*"),
        Pattern.compile("(?i).*[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+.*"), // IP addresses
        Pattern.compile("(?i).*(discord\\.gg|discord\\.com).*"), // Discord invites
        Pattern.compile("(?i).*(youtube\\.com|youtu\\.be).*"), // YouTube links (optional)
        Pattern.compile("(?i).*\\b(hack|cheat|x-ray|xray|grief|dupe)\\b.*") // Cheating terms
    ));
    
    public static boolean containsInappropriateContent(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }
        
        String cleanMessage = message.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
        
        // Check for inappropriate words
        for (String word : INAPPROPRIATE_WORDS) {
            if (cleanMessage.contains(word.toLowerCase())) {
                return true;
            }
        }
        
        // Check for inappropriate patterns
        for (Pattern pattern : INAPPROPRIATE_PATTERNS) {
            if (pattern.matcher(message).matches()) {
                return true;
            }
        }
        
        // Check for excessive caps (more than 70% uppercase)
        if (isExcessiveCaps(message)) {
            return true;
        }
        
        // Check for spam (repeated characters)
        if (isSpam(message)) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isExcessiveCaps(String message) {
        if (message.length() < 5) return false;
        
        int upperCount = 0;
        int letterCount = 0;
        
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
                if (Character.isUpperCase(c)) {
                    upperCount++;
                }
            }
        }
        
        if (letterCount == 0) return false;
        
        double capsPercentage = (double) upperCount / letterCount;
        return capsPercentage > 0.7;
    }
    
    private static boolean isSpam(String message) {
        if (message.length() < 10) return false;
        
        // Check for repeated characters (more than 4 in a row)
        Pattern spamPattern = Pattern.compile("(.)\\1{4,}");
        return spamPattern.matcher(message).find();
    }
    
    public static String cleanMessage(String message) {
        if (message == null) return "";
        
        // Replace inappropriate words with asterisks
        String cleaned = message;
        for (String word : INAPPROPRIATE_WORDS) {
            String replacement = "*".repeat(word.length());
            cleaned = cleaned.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", replacement);
        }
        
        return cleaned;
    }
}