package com.dcombat.manager;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Statut de tag PvP, purement en mémoire (état transitoire, comme TpaManager
 * dans DCore) : un tag n'a aucun sens à survivre à un redémarrage.
 */
public class TagManager {

    private final Map<UUID, Long> taggedUntil = new HashMap<>();

    private long tagDurationMillis;
    private boolean killOnCombatLog;

    public void loadConfig(FileConfiguration cfg) {
        this.tagDurationMillis = Math.max(1, cfg.getInt("combat.tag-duration-seconds", 15)) * 1000L;
        this.killOnCombatLog = cfg.getBoolean("combat.kill-on-combat-log", true);
    }

    public boolean isKillOnCombatLog() {
        return killOnCombatLog;
    }

    public void tag(UUID playerId) {
        taggedUntil.put(playerId, System.currentTimeMillis() + tagDurationMillis);
    }

    public boolean isTagged(UUID playerId) {
        Long until = taggedUntil.get(playerId);
        return until != null && until > System.currentTimeMillis();
    }

    public void clear(UUID playerId) {
        taggedUntil.remove(playerId);
    }
}
