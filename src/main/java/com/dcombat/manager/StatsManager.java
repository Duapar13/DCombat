package com.dcombat.manager;

import com.dcombat.model.PlayerStats;
import com.dcombat.storage.CombatStorage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToIntFunction;

public class StatsManager {

    private final CombatStorage storage;
    private final Map<UUID, PlayerStats> stats = new HashMap<>();

    private int streakAnnounceEvery;

    public StatsManager(CombatStorage storage) {
        this.storage = storage;
    }

    public void loadConfig(FileConfiguration cfg) {
        this.streakAnnounceEvery = Math.max(0, cfg.getInt("combat.streak-announce-every", 5));
    }

    public int getStreakAnnounceEvery() {
        return streakAnnounceEvery;
    }

    public void seed(Map<UUID, PlayerStats> loaded) {
        stats.clear();
        stats.putAll(loaded);
    }

    public PlayerStats getOrCreate(UUID playerId) {
        return stats.computeIfAbsent(playerId, id -> new PlayerStats(id, 0, 0, 0, 0));
    }

    public PlayerStats registerKill(UUID killerId) {
        PlayerStats killerStats = getOrCreate(killerId);
        killerStats.registerKill();
        storage.saveStats(killerStats);
        return killerStats;
    }

    public PlayerStats registerDeath(UUID victimId) {
        PlayerStats victimStats = getOrCreate(victimId);
        victimStats.registerDeath();
        storage.saveStats(victimStats);
        return victimStats;
    }

    public List<PlayerStats> top(ToIntFunction<PlayerStats> metric, int limit) {
        List<PlayerStats> result = new ArrayList<>(stats.values());
        result.sort(Comparator.comparingInt(metric).reversed());
        if (result.size() > limit) {
            return result.subList(0, limit);
        }
        return result;
    }
}
