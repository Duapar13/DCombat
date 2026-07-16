package com.dcombat.listeners;

import com.dcombat.manager.StatsManager;
import com.dcombat.manager.TagManager;
import com.dcombat.model.PlayerStats;
import com.dcombat.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillTrackListener implements Listener {

    private final StatsManager statsManager;
    private final TagManager tagManager;

    public KillTrackListener(StatsManager statsManager, TagManager tagManager) {
        this.statsManager = statsManager;
        this.tagManager = tagManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        tagManager.clear(victim.getUniqueId());
        statsManager.registerDeath(victim.getUniqueId());

        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) {
            return;
        }
        tagManager.clear(killer.getUniqueId());
        PlayerStats killerStats = statsManager.registerKill(killer.getUniqueId());

        int every = statsManager.getStreakAnnounceEvery();
        if (every > 0 && killerStats.getStreak() > 0 && killerStats.getStreak() % every == 0) {
            Bukkit.broadcastMessage(Msg.PREFIX + ChatColor.GOLD + killer.getName()
                    + ChatColor.YELLOW + " est en série de " + ChatColor.RED + killerStats.getStreak()
                    + ChatColor.YELLOW + " kills !");
        }
    }
}
