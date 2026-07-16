package com.dcombat.listeners;

import com.dcombat.manager.TagManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatTagListener implements Listener {

    private final TagManager tagManager;

    public CombatTagListener(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getEntity();
        Player attacker = resolvePlayer(event.getDamager());
        if (attacker == null || attacker.equals(victim)) {
            return;
        }
        tagManager.tag(victim.getUniqueId());
        tagManager.tag(attacker.getUniqueId());
    }

    private Player resolvePlayer(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile) {
            Object shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }
}
