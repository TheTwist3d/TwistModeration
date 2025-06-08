package com.twistmoderation.database;

import com.twistmoderation.TwistModeration;
import com.twistmoderation.models.Punishment;
import com.twistmoderation.models.PunishmentType;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    
    private final TwistModeration plugin;
    private Connection connection;
    
    public DatabaseManager(TwistModeration plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() throws SQLException {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        String url = "jdbc:sqlite:" + new File(dataFolder, "twistmoderation.db").getAbsolutePath();
        connection = DriverManager.getConnection(url);
        
        createTables();
    }
    
    private void createTables() throws SQLException {
        String punishmentsTable = """
            CREATE TABLE IF NOT EXISTS punishments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_uuid TEXT NOT NULL,
                player_name TEXT NOT NULL,
                punishment_type TEXT NOT NULL,
                reason TEXT NOT NULL,
                punisher_uuid TEXT,
                punisher_name TEXT,
                timestamp BIGINT NOT NULL,
                duration BIGINT DEFAULT 0,
                active BOOLEAN DEFAULT 1,
                removed_by TEXT,
                removed_at BIGINT DEFAULT 0
            )
        """;
        
        String mutesTable = """
            CREATE TABLE IF NOT EXISTS active_mutes (
                player_uuid TEXT PRIMARY KEY,
                player_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                punisher_name TEXT,
                timestamp BIGINT NOT NULL,
                duration BIGINT DEFAULT 0
            )
        """;
        
        String bansTable = """
            CREATE TABLE IF NOT EXISTS active_bans (
                player_uuid TEXT PRIMARY KEY,
                player_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                punisher_name TEXT,
                timestamp BIGINT NOT NULL,
                duration BIGINT DEFAULT 0
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(punishmentsTable);
            stmt.execute(mutesTable);
            stmt.execute(bansTable);
        }
    }
    
    public void addPunishment(Punishment punishment) throws SQLException {
        String sql = """
            INSERT INTO punishments (player_uuid, player_name, punishment_type, reason, 
                                   punisher_uuid, punisher_name, timestamp, duration, active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, punishment.getPlayerUuid().toString());
            stmt.setString(2, punishment.getPlayerName());
            stmt.setString(3, punishment.getType().name());
            stmt.setString(4, punishment.getReason());
            stmt.setString(5, punishment.getPunisherUuid() != null ? punishment.getPunisherUuid().toString() : null);
            stmt.setString(6, punishment.getPunisherName());
            stmt.setLong(7, punishment.getTimestamp());
            stmt.setLong(8, punishment.getDuration());
            stmt.setBoolean(9, punishment.isActive());
            stmt.executeUpdate();
        }
    }
    
    public void addActiveMute(UUID playerUuid, String playerName, String reason, String punisherName, long duration) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO active_mutes (player_uuid, player_name, reason, punisher_name, timestamp, duration)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, playerName);
            stmt.setString(3, reason);
            stmt.setString(4, punisherName);
            stmt.setLong(5, System.currentTimeMillis());
            stmt.setLong(6, duration);
            stmt.executeUpdate();
        }
    }
    
    public void removeActiveMute(UUID playerUuid) throws SQLException {
        String sql = "DELETE FROM active_mutes WHERE player_uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.executeUpdate();
        }
    }
    
    public boolean isPlayerMuted(UUID playerUuid) throws SQLException {
        String sql = "SELECT timestamp, duration FROM active_mutes WHERE player_uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                long timestamp = rs.getLong("timestamp");
                long duration = rs.getLong("duration");
                
                // Check if temporary mute has expired
                if (duration > 0 && System.currentTimeMillis() > timestamp + (duration * 1000)) {
                    removeActiveMute(playerUuid);
                    return false;
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    public String getMuteReason(UUID playerUuid) throws SQLException {
        String sql = "SELECT reason FROM active_mutes WHERE player_uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("reason");
            }
        }
        
        return null;
    }
    
    public List<Punishment> getPlayerHistory(UUID playerUuid) throws SQLException {
        String sql = """
            SELECT * FROM punishments 
            WHERE player_uuid = ? 
            ORDER BY timestamp DESC
        """;
        
        List<Punishment> history = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Punishment punishment = new Punishment(
                    UUID.fromString(rs.getString("player_uuid")),
                    rs.getString("player_name"),
                    PunishmentType.valueOf(rs.getString("punishment_type")),
                    rs.getString("reason"),
                    rs.getString("punisher_uuid") != null ? UUID.fromString(rs.getString("punisher_uuid")) : null,
                    rs.getString("punisher_name"),
                    rs.getLong("timestamp"),
                    rs.getLong("duration")
                );
                punishment.setActive(rs.getBoolean("active"));
                history.add(punishment);
            }
        }
        
        return history;
    }
    
    public int getWarningCount(UUID playerUuid) throws SQLException {
        String sql = """
            SELECT COUNT(*) as count FROM punishments 
            WHERE player_uuid = ? AND punishment_type = 'WARN' AND active = 1
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }
    
    public int getKickCount(UUID playerUuid) throws SQLException {
        String sql = """
            SELECT COUNT(*) as count FROM punishments 
            WHERE player_uuid = ? AND punishment_type = 'KICK'
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
        }
    }
}