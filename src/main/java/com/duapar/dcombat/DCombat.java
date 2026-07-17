package com.duapar.dcombat;

import com.duapar.dcombat.commands.CombatCommand;
import com.duapar.dcombat.integration.DAPIHook;
import com.duapar.dcombat.listeners.CombatLogListener;
import com.duapar.dcombat.listeners.CombatTagListener;
import com.duapar.dcombat.listeners.KillTrackListener;
import com.duapar.dcombat.manager.StatsManager;
import com.duapar.dcombat.manager.TagManager;
import com.duapar.dcombat.storage.CombatStorage;
import com.duapar.dcombat.storage.MySQLCombatStorage;
import com.duapar.dcombat.storage.YamlCombatStorage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class DCombat extends JavaPlugin {

    private CombatStorage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String storageType = getConfig().getString("storage.type", "local");
        if ("mysql".equalsIgnoreCase(storageType)) {
            storage = new MySQLCombatStorage(this,
                    getConfig().getString("storage.mysql.host", "localhost"),
                    getConfig().getInt("storage.mysql.port", 3306),
                    getConfig().getString("storage.mysql.database", "dcombat"),
                    getConfig().getString("storage.mysql.username", "root"),
                    getConfig().getString("storage.mysql.password", ""),
                    getConfig().getBoolean("storage.mysql.useSSL", false));
        } else {
            storage = new YamlCombatStorage(getDataFolder(), getLogger());
        }

        try {
            storage.init();
        } catch (Exception e) {
            getLogger().severe("Impossible d'initialiser le stockage (" + storageType + "): " + e.getMessage());
            getLogger().severe("Le plugin va se désactiver.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        TagManager tagManager = new TagManager();
        tagManager.loadConfig(getConfig());
        StatsManager statsManager = new StatsManager(storage);
        statsManager.loadConfig(getConfig());

        try {
            statsManager.seed(storage.loadStats());
        } catch (Exception e) {
            getLogger().severe("Erreur lors du chargement des statistiques: " + e.getMessage());
        }

        CombatCommand combatCommand = new CombatCommand(statsManager);
        PluginCommand kills = getCommand("kills");
        if (kills != null) {
            kills.setExecutor(combatCommand);
        }
        PluginCommand pvptop = getCommand("pvptop");
        if (pvptop != null) {
            pvptop.setExecutor(combatCommand);
        }

        getServer().getPluginManager().registerEvents(new CombatTagListener(tagManager), this);
        getServer().getPluginManager().registerEvents(new CombatLogListener(tagManager), this);
        getServer().getPluginManager().registerEvents(new KillTrackListener(statsManager, tagManager), this);

        if (getServer().getPluginManager().isPluginEnabled("DAPI")) {
            DAPIHook.registerCombatService(this, tagManager, statsManager);
        } else {
            getLogger().info("DAPI non détecté : DCombat fonctionne en mode autonome (CombatService non partagé).");
        }

        getLogger().info("DCombat activé (stockage: " + storageType + ").");
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.close();
        }
    }
}
