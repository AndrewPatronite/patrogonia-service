package com.patronite.service.dto;

import com.patronite.service.battle.BattleStatus;
import com.patronite.service.battle.LogEntry;
import com.patronite.service.battle.turn.PlayerAction;
import com.patronite.service.dto.enemy.EnemyDto;
import com.patronite.service.dto.item.RewardDto;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.StatsDto;

import java.beans.Transient;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class BattleDto implements Serializable {
    private static final long serialVersionUID = -7924483845986265569L;
    private final UUID id = UUID.randomUUID();
    private List<EnemyDto> enemies;
    private Map<Integer, StatsDto> playerStats = new ConcurrentHashMap<>();
    private List<LogEntry> log = newArrayList();
    private Map<Integer, PlayerAction> roundPlayerActions = new ConcurrentHashMap<>();
    private Map<Integer, List<RewardDto>> playerRewards = new ConcurrentHashMap<>();
    private final LocationDto location;
    private BattleStatus status = BattleStatus.IN_PROGRESS;
    private int round = 0;

    public BattleDto(List<EnemyDto> enemies, PlayerDto player) {
        log.add(new LogEntry(round, "Enemies approach.", true));
        playerStats.put(player.getId(), player.getStats());
        this.enemies = enemies;
        location = player.getLocation();
    }

    public UUID getId() {
        return id;
    }

    public List<EnemyDto> getEnemies() {
        return enemies;
    }

    public Map<Integer, StatsDto> getPlayerStats() {
        return playerStats;
    }

    public List<LogEntry> getLog() {
        return log;
    }

    public void submitTurn(int playerId, PlayerAction playerAction) {
        roundPlayerActions.put(playerId, playerAction);
    }

    public boolean allTurnsIn() {
        return roundPlayerActions.keySet().equals(playerStats.keySet());
    }

    public Map<Integer, PlayerAction> getRoundPlayerActions() {
        return roundPlayerActions;
    }

    public void clearPlayerActions() {
        roundPlayerActions.clear();
    }

    public void addLogEntry(String entry) {
        log.add(new LogEntry(round, entry, false));
    }

    public void addLogEntry(String entry, int targetId) {
        log.add(new LogEntry(round, entry, false, Integer.toString(targetId)));
    }

    public void addLogEntry(String entry, UUID targetId) {
        log.add(new LogEntry(round, entry, false, targetId.toString()));
    }

    public void defeatEnemyAndAllotRewardToPlayer(EnemyDto enemy, int playerId) {
        addLogEntry(String.format("%s is defeated.", enemy.getName()), enemy.getId());
        RewardDto enemyReward = enemy.getRewardDto();
        playerRewards.putIfAbsent(playerId, newArrayList());
        playerRewards.get(playerId).add(enemyReward);
    }

    @Transient
    public Map<Integer, List<RewardDto>> getPlayerRewards() {
        return playerRewards;
    }

    public BattleStatus getStatus() {
        return status;
    }

    @Transient
    public void setStatus(BattleStatus status) {
        this.status = status;
    }

    @Transient
    public List<StatsDto> getLivingPlayers() {
        return getPlayerStats().values().stream()
                .filter(playerStat -> !playerStat.isDead())
                .collect(Collectors.toList());
    }

    @Transient
    public List<EnemyDto> getLivingEnemies() {
        return getEnemies().stream()
                .filter(enemy -> !enemy.isDefeated())
                .collect(Collectors.toList());
    }

    @Transient
    public List<StatsDto> getDeadPlayers() {
        return getPlayerStats().values().stream()
                .filter(StatsDto::isDead)
                .collect(Collectors.toList());
    }

    @Transient
    public void addPlayer(StatsDto player) {
        playerStats.put(player.getPlayerId(), player);
    }

    public LocationDto getLocation() {
        return location;
    }

    public int startRound() {
        return ++round;
    }
}
