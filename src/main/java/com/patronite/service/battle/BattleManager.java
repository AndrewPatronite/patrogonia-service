package com.patronite.service.battle;

import com.google.common.collect.Sets;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.level.Level;
import com.patronite.service.level.LevelManager;
import com.patronite.service.model.Location;
import com.patronite.service.save.SaveManager;
import com.patronite.service.battle.turn.PlayerAction;
import com.patronite.service.battle.turn.RoundManager;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.message.BattleMessenger;
import com.patronite.service.spell.Spell;
import com.patronite.service.stats.StatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class BattleManager {
    private static final int BATTLE_PLAYER_LIMIT = 4;
    public static final int MAX_WAIT_FOR_OTHER_PLAYERS = 10;
    private final Map<UUID, BattleDto> battles = new ConcurrentHashMap<>();
    private final BattleGenerator battleGenerator;
    private final BattleMessenger battleMessenger;
    private final RoundManager roundManager;
    private final SpoilsManager spoilsManager;
    private final SaveManager saveManager;
    private final StatsManager statsManager;
    private final LevelManager levelManager;
    private static final Logger logger = LoggerFactory.getLogger(BattleManager.class);

    public BattleManager(BattleGenerator battleGenerator, BattleMessenger battleMessenger,
                         RoundManager roundManager, SpoilsManager spoilsManager, SaveManager saveManager, StatsManager statsManager, LevelManager levelManager) {
        this.battleGenerator = battleGenerator;
        this.battleMessenger = battleMessenger;
        this.roundManager = roundManager;
        this.spoilsManager = spoilsManager;
        this.saveManager = saveManager;
        this.statsManager = statsManager;
        this.levelManager = levelManager;
    }

    public Optional<BattleDto> spawnOrDontSpawnBattle(PlayerDto playerDto, Location lastLocation) {
        Optional<BattleDto> potentialBattle = battleGenerator.spawnOrDontSpawnBattle(playerDto, lastLocation);
        potentialBattle.ifPresent(battle -> {
            removePlayerFromBattle(playerDto.getId());
            battles.put(battle.getId(), battle);
        });
        return potentialBattle;
    }

    public Optional<BattleDto> findBattleInProgressAtLocation(LocationDto location) {
        return battles.values().stream()
                .filter(battleDto -> battleDto.getLocation().equals(location)
                        && battleDto.getPlayerStats().size() < BATTLE_PLAYER_LIMIT)
                .findFirst();
    }

    public void takeTurn(String battleId, int playerId, String playerAction, String targetId) {
        UUID battleUuid = UUID.fromString(battleId);
        BattleDto battle = battles.get(battleUuid);
        synchronized (battle.getId()) {
            battle.submitTurn(playerId, new PlayerAction(playerAction, targetId));
            battleMessenger.publishBattleMessage(battle);

            if (battle.allTurnsIn()) {
                conductRound(battle);
            } else {
                Executors.newSingleThreadScheduledExecutor().schedule(() -> conductRound(battle), MAX_WAIT_FOR_OTHER_PLAYERS, TimeUnit.SECONDS);
            }
        }
    }

    private void conductRound(BattleDto battle) {
        try {
            synchronized (battle.getId()) {
                if (battle.getStatus().equals(BattleStatus.IN_PROGRESS)) {
                    BattleStatus battleStatus = roundManager.conductRound(battle);
                    battle.clearPlayerActions();
                    handleBattleStatus(battle, battleStatus);
                    battleMessenger.publishBattleMessagesForCurrentRound(battle);
                }
            }
        } catch (Exception ex) {
            logger.error("A problem occurred while conducting the round: ", ex);
        }
    }

    public void joinBattle(StatsDto player, UUID battleUuid) {
        BattleDto battle = battles.get(battleUuid);
        //TODO AP not sure why this would be null
        if ( battle != null) {
            removePlayerFromBattle(player.getPlayerId());
            battle.addPlayer(player);
            battle.addLogEntry(String.format("%s joins the battle!", player.getPlayerName()));
            battleMessenger.publishBattleMessage(battle);
        }
    }

    public void removePlayerFromBattle(int playerId) {
        Set<UUID> battlesToRemove = Sets.newHashSet();
        battles.values().stream()
                .filter(battle -> battle.getPlayerStats().containsKey(playerId))
                .forEach(battleDto -> {
                    battleDto.getRoundPlayerActions().remove(playerId);
                    battleDto.getPlayerStats().remove(playerId);
                    battleDto.getPlayerRewards().remove(playerId);
                    if (battleDto.getPlayerStats().isEmpty()) {
                        battlesToRemove.add(battleDto.getId());
                    }
                });
        battles.keySet().removeAll(battlesToRemove);
    }

    private void handleBattleStatus(BattleDto battle, BattleStatus battleStatus) {
        switch (battleStatus) {
            case VICTORY:
                spoilsManager.distributeSpoils(battle);
                updateLivingPlayersStats(battle);
                sendDeadBackToLastSave(battle.getDeadPlayers());
                battles.remove(battle.getId());
                break;
            case DEFEAT:
                sendDeadBackToLastSave(battle.getDeadPlayers());
                battles.remove(battle.getId());
                break;
            case IN_PROGRESS:
            default:
                break;
        }
    }

    private void updateLivingPlayersStats(BattleDto battle) {
        battle.getLivingPlayers().forEach(player -> {
            applyAnyLevelPromotions(battle, player);
            statsManager.updateStats(player.getPlayerId(), player);
        });
    }

    private void applyAnyLevelPromotions(BattleDto battle, StatsDto player) {
        levelManager.promoteToHigherLevel(player.getLevel(), player.getXp())
                .ifPresent(nextLevel -> {
                    int hpIncrease = nextLevel.getHpTotal() - player.getHpTotal();
                    int mpIncrease = nextLevel.getMpTotal() - player.getMpTotal();
                    int attackIncrease = nextLevel.getAttackTotal() - player.getAttack();
                    int defenseIncrease = nextLevel.getDefenseTotal() - player.getDefense();
                    int agilityIncrease = nextLevel.getAgilityTotal() - player.getAgility();
                    player.setLevel(nextLevel.getLevel());
                    player.setHpTotal(nextLevel.getHpTotal());
                    player.setMpTotal(nextLevel.getMpTotal());
                    player.setAttack(nextLevel.getAttackTotal());
                    player.setDefense(nextLevel.getDefenseTotal());
                    player.setAgility(nextLevel.getAgilityTotal());
                    battle.addLogEntry(String.format("%s has reached level %s!", player.getPlayerName(), nextLevel.getLevel()));
                    reportIncrease(battle, "HP", hpIncrease);
                    reportIncrease(battle, "MP", mpIncrease);
                    reportIncrease(battle, "Attack", attackIncrease);
                    reportIncrease(battle, "Defense", defenseIncrease);
                    reportIncrease(battle, "Agility", agilityIncrease);
                    reportNewSpell(battle, player, nextLevel);
                });
    }

    private void reportNewSpell(BattleDto battle, StatsDto player, Level nextLevel) {
        nextLevel.getSpellsLearned().forEach(newSpell -> {
            String spellName = newSpell.name();
            battle.addLogEntry(String.format("%s learned %s%s spell!", player.getPlayerName(), spellName.substring(0, 1), spellName.substring(1).toLowerCase()));
        });
    }

    private void reportIncrease(BattleDto battle, String attribute, int increase) {
        if (increase > 0) {
            battle.addLogEntry(String.format("%s increased by %s.", attribute, increase));
        }
    }

    private void sendDeadBackToLastSave(List<StatsDto> deadPlayers) {
        deadPlayers
                .forEach(playerStat -> saveManager.loadLastSave(playerStat.getPlayerId()));
    }

    public BattleDto getBattle(String battleId) {
        return battles.get(UUID.fromString(battleId));
    }

    public Optional<UUID> getPlayerBattleId(int playerId) {
        return battles.values().stream()
                .filter(battleDto -> battleDto.getLivingPlayers().stream()
                        .anyMatch(combatant -> combatant.getPlayerId() == playerId))
                .map(BattleDto::getId)
                .findFirst();
    }
}
