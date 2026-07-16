package com.dcombat.storage;

import com.dcombat.model.PlayerStats;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlCombatStorage implements CombatStorage {

    private final File file;
    private final Logger logger;
    private YamlConfiguration config;

    public YamlCombatStorage(File dataFolder, Logger logger) {
        this.file = new File(new File(dataFolder, "data"), "stats.yml");
        this.logger = logger;
    }

    @Override
    public void init() throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Impossible de créer le dossier de données " + dir);
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Impossible de créer " + file);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public Map<UUID, PlayerStats> loadStats() {
        Map<UUID, PlayerStats> result = new HashMap<>();
        ConfigurationSection root = config.getConfigurationSection("stats");
        if (root == null) {
            return result;
        }
        for (String uuidStr : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(uuidStr);
            if (section == null) continue;
            try {
                UUID playerId = UUID.fromString(uuidStr);
                result.put(playerId, new PlayerStats(playerId,
                        section.getInt("kills"), section.getInt("deaths"),
                        section.getInt("streak"), section.getInt("bestStreak")));
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "UUID de statistiques invalide ignoré: " + uuidStr);
            }
        }
        return result;
    }

    @Override
    public synchronized void saveStats(PlayerStats stats) {
        String base = "stats." + stats.getPlayerId();
        config.set(base + ".kills", stats.getKills());
        config.set(base + ".deaths", stats.getDeaths());
        config.set(base + ".streak", stats.getStreak());
        config.set(base + ".bestStreak", stats.getBestStreak());
        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Impossible de sauvegarder " + file, e);
        }
    }

    @Override
    public void close() {
        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Impossible de sauvegarder " + file, e);
        }
    }
}
