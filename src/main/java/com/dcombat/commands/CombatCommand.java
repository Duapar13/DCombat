package com.dcombat.commands;

import com.dcombat.manager.CombatException;
import com.dcombat.manager.StatsManager;
import com.dcombat.model.PlayerStats;
import com.dcombat.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Gère /kills [joueur] et /pvptop [kills|deaths|streak] (même instance
 * enregistrée pour les deux).
 */
public class CombatCommand implements CommandExecutor {

    private final StatsManager statsManager;

    public CombatCommand(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("dcombat.use")) {
            Msg.error(sender, "Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }

        try {
            if ("pvptop".equalsIgnoreCase(command.getName())) {
                handleTop(sender, args);
            } else {
                handleKills(sender, args);
            }
        } catch (CombatException e) {
            Msg.error(sender, e.getMessage());
        }
        return true;
    }

    private void handleKills(CommandSender sender, String[] args) {
        UUID targetId;
        String targetName;
        if (args.length > 0) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            targetId = target.getUniqueId();
            targetName = target.getName() != null ? target.getName() : args[0];
        } else if (sender instanceof Player) {
            targetId = ((Player) sender).getUniqueId();
            targetName = sender.getName();
        } else {
            throw new CombatException("Utilisation: /kills <joueur>");
        }
        PlayerStats stats = statsManager.getOrCreate(targetId);
        sender.sendMessage(ChatColor.DARK_GRAY + "==== " + ChatColor.YELLOW + targetName + ChatColor.DARK_GRAY + " ====");
        sender.sendMessage(ChatColor.GRAY + "Kills: " + ChatColor.WHITE + stats.getKills());
        sender.sendMessage(ChatColor.GRAY + "Morts: " + ChatColor.WHITE + stats.getDeaths());
        sender.sendMessage(ChatColor.GRAY + "Ratio K/D: " + ChatColor.WHITE + String.format(Locale.ROOT, "%.2f", stats.getKdr()));
        sender.sendMessage(ChatColor.GRAY + "Série actuelle: " + ChatColor.WHITE + stats.getStreak());
        sender.sendMessage(ChatColor.GRAY + "Meilleure série: " + ChatColor.WHITE + stats.getBestStreak());
    }

    private void handleTop(CommandSender sender, String[] args) {
        String metric = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "kills";
        List<PlayerStats> top;
        String label;
        switch (metric) {
            case "deaths":
                top = statsManager.top(PlayerStats::getDeaths, 10);
                label = "Morts";
                break;
            case "streak":
                top = statsManager.top(PlayerStats::getBestStreak, 10);
                label = "Meilleure série";
                break;
            case "kills":
                top = statsManager.top(PlayerStats::getKills, 10);
                label = "Kills";
                break;
            default:
                throw new CombatException("Utilisation: /pvptop [kills|deaths|streak]");
        }
        sender.sendMessage(ChatColor.DARK_GRAY + "==== " + ChatColor.YELLOW + "Classement PvP - " + label + ChatColor.DARK_GRAY + " ====");
        if (top.isEmpty()) {
            Msg.send(sender, "Aucune statistique pour le moment.");
            return;
        }
        int rank = 1;
        for (PlayerStats stats : top) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(stats.getPlayerId());
            String name = player.getName() != null ? player.getName() : stats.getPlayerId().toString();
            int value = "deaths".equals(metric) ? stats.getDeaths() : "streak".equals(metric) ? stats.getBestStreak() : stats.getKills();
            sender.sendMessage(ChatColor.GOLD + "#" + rank + " " + ChatColor.WHITE + name + ChatColor.GRAY + " - " + value);
            rank++;
        }
    }
}
