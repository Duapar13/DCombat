package com.dcombat.storage;

import com.dcombat.model.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLCombatStorage implements CombatStorage {

    private final JavaPlugin plugin;
    private final String url;
    private final String username;
    private final String password;

    private Connection connection;

    public MySQLCombatStorage(JavaPlugin plugin, String host, int port, String database,
                               String username, String password, boolean useSSL) {
        this.plugin = plugin;
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=" + useSSL + "&allowPublicKeyRetrieval=true&autoReconnect=true";
        this.username = username;
        this.password = password;
    }

    @Override
    public void init() throws Exception {
        Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
        connect();
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS dcombat_stats (" +
                    "player_uuid VARCHAR(36) PRIMARY KEY," +
                    "kills INT NOT NULL," +
                    "deaths INT NOT NULL," +
                    "streak INT NOT NULL," +
                    "best_streak INT NOT NULL)");
        }
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
    }

    @Override
    public Map<UUID, PlayerStats> loadStats() throws SQLException {
        Map<UUID, PlayerStats> result = new HashMap<>();
        connect();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM dcombat_stats")) {
            while (rs.next()) {
                UUID playerId = UUID.fromString(rs.getString("player_uuid"));
                result.put(playerId, new PlayerStats(playerId,
                        rs.getInt("kills"), rs.getInt("deaths"), rs.getInt("streak"), rs.getInt("best_streak")));
            }
        }
        return result;
    }

    @Override
    public void saveStats(PlayerStats stats) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                connect();
                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO dcombat_stats (player_uuid, kills, deaths, streak, best_streak) VALUES (?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE kills = VALUES(kills), deaths = VALUES(deaths), " +
                                "streak = VALUES(streak), best_streak = VALUES(best_streak)")) {
                    ps.setString(1, stats.getPlayerId().toString());
                    ps.setInt(2, stats.getKills());
                    ps.setInt(3, stats.getDeaths());
                    ps.setInt(4, stats.getStreak());
                    ps.setInt(5, stats.getBestStreak());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Erreur MySQL lors de la sauvegarde des stats de " + stats.getPlayerId(), e);
            }
        });
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur lors de la fermeture de la connexion MySQL", e);
        }
    }
}
