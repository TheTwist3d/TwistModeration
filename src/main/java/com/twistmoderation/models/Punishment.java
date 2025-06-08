package com.twistmoderation.models;

import java.util.UUID;

public class Punishment {
    
    private final UUID playerUuid;
    private final String playerName;
    private final PunishmentType type;
    private final String reason;
    private final UUID punisherUuid;
    private final String punisherName;
    private final long timestamp;
    private final long duration; // in seconds, 0 for permanent
    private boolean active;
    
    public Punishment(UUID playerUuid, String playerName, PunishmentType type, String reason,
                     UUID punisherUuid, String punisherName, long timestamp, long duration) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.reason = reason;
        this.punisherUuid = punisherUuid;
        this.punisherName = punisherName;
        this.timestamp = timestamp;
        this.duration = duration;
        this.active = true;
    }
    
    // Getters
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public PunishmentType getType() { return type; }
    public String getReason() { return reason; }
    public UUID getPunisherUuid() { return punisherUuid; }
    public String getPunisherName() { return punisherName; }
    public long getTimestamp() { return timestamp; }
    public long getDuration() { return duration; }
    public boolean isActive() { return active; }
    
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isPermanent() {
        return duration == 0;
    }
    
    public boolean isExpired() {
        if (isPermanent()) return false;
        return System.currentTimeMillis() > timestamp + (duration * 1000);
    }
}