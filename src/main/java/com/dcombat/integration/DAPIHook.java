package com.dcombat.integration;

import com.dapi.DAPI;
import com.dapi.service.CombatService;
import com.dcombat.manager.StatsManager;
import com.dcombat.manager.TagManager;
import com.dcombat.service.DCombatServiceImpl;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * DCombat fonctionne entièrement sans DAPI (softdepend: [DAPI], comme DCore
 * et DClass) : l'intégration n'apporte qu'un bonus (CombatService pour les
 * autres plugins D(nom)), jamais une fonction indispensable.
 */
public final class DAPIHook {

    private DAPIHook() {
    }

    public static void registerCombatService(JavaPlugin plugin, TagManager tagManager, StatsManager statsManager) {
        DAPI.registerPlugin(plugin, "CombatService");
        DAPI.registerService(CombatService.class, new DCombatServiceImpl(tagManager, statsManager), plugin);
    }
}
