package com.dcombat.listeners;

import com.dcombat.manager.TagManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Punit le combat-log (se déconnecter pour échapper à un combat) : le joueur
 * tagué qui se déconnecte est tué, comme une mort normale (perte d'objets).
 */
public class CombatLogListener implements Listener {

    private final TagManager tagManager;

    public CombatLogListener(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (tagManager.isKillOnCombatLog() && tagManager.isTagged(event.getPlayer().getUniqueId())) {
            event.getPlayer().setHealth(0);
        }
    }
}
