package com.duapar.dcombat.service;

import com.duapar.dapi.service.CombatService;
import com.duapar.dcombat.manager.StatsManager;
import com.duapar.dcombat.manager.TagManager;

import java.util.UUID;

public class DCombatServiceImpl implements CombatService {

    private final TagManager tagManager;
    private final StatsManager statsManager;

    public DCombatServiceImpl(TagManager tagManager, StatsManager statsManager) {
        this.tagManager = tagManager;
        this.statsManager = statsManager;
    }

    @Override
    public boolean isTagged(UUID playerId) {
        return tagManager.isTagged(playerId);
    }

    @Override
    public int getKills(UUID playerId) {
        return statsManager.getOrCreate(playerId).getKills();
    }

    @Override
    public int getDeaths(UUID playerId) {
        return statsManager.getOrCreate(playerId).getDeaths();
    }

    @Override
    public int getStreak(UUID playerId) {
        return statsManager.getOrCreate(playerId).getStreak();
    }
}
