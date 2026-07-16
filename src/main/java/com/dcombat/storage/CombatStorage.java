package com.dcombat.storage;

import com.dcombat.model.PlayerStats;

import java.util.Map;
import java.util.UUID;

public interface CombatStorage {

    void init() throws Exception;

    /**
     * Charge toutes les statistiques connues. Clé de la map = joueur.
     */
    Map<UUID, PlayerStats> loadStats() throws Exception;

    void saveStats(PlayerStats stats);

    void close();
}
