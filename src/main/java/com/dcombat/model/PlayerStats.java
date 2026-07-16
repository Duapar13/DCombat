package com.dcombat.model;

import java.util.UUID;

public class PlayerStats {

    private final UUID playerId;
    private int kills;
    private int deaths;
    private int streak;
    private int bestStreak;

    public PlayerStats(UUID playerId, int kills, int deaths, int streak, int bestStreak) {
        this.playerId = playerId;
        this.kills = kills;
        this.deaths = deaths;
        this.streak = streak;
        this.bestStreak = bestStreak;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getStreak() {
        return streak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public double getKdr() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public void registerKill() {
        kills++;
        streak++;
        bestStreak = Math.max(bestStreak, streak);
    }

    public void registerDeath() {
        deaths++;
        streak = 0;
    }
}
