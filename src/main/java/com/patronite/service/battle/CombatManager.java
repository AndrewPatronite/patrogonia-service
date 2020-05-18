package com.patronite.service.battle;

import com.patronite.service.battle.turn.PlayerAction;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.enemy.EnemyDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.spell.Spell;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CombatManager {
    private static final int PARRY_DEFENSE_MULTIPLIER = 2;
    private static final String ATTACK = "attack";
    private static final String PARRY = "parry";
    private static final String RUN = "run";

    public void fightEnemy(BattleDto battle, int playerId) {
        StatsDto playerStats = battle.getPlayerStats().get(playerId);
        PlayerAction playerAction = battle.getRoundPlayerActions().get(playerId);
        if (playerAction != null) {
            switch (playerAction.getAction()) {
                case ATTACK:
                    getTargetedOrFirstEnemy(playerAction.getTargetId(), battle)
                            .ifPresent(targetedOrFirstEnemy ->
                                    attackEnemy(battle, playerStats, targetedOrFirstEnemy));
                    break;
                case PARRY:
                    battle.addLogEntry(String.format("%s blocks.", playerStats.getPlayerName()));
                    break;
                case RUN:
                    //TODO let players run based on their agility relative to enemies, boss fights, etc.
                    battle.addLogEntry(String.format("%s tries to run but there is no escape.", playerStats.getPlayerName()));
                    break;
                default:
                    castSpell(playerAction.getAction(), playerAction.getTargetId(), battle, playerStats);
                    break;
            }
        }
    }

    public void fightPlayer(BattleDto battle, UUID enemyId) {
        List<StatsDto> playerStats = battle.getLivingPlayers();
        StatsDto player = playerStats.get(new Random().nextInt(playerStats.size()));
        battle.getLivingEnemies().stream()
                .filter(enemy -> enemy.getId().equals(enemyId))
                .findFirst()
                .ifPresent(enemy -> attackPlayer(battle, enemy, player));
    }

    private void attackPlayer(BattleDto battle, EnemyDto enemy, StatsDto player) {
        int damage = getDamageToPlayer(enemy, player, battle.getRoundPlayerActions().get(player.getPlayerId()));
        player.damage(damage);
        battle.addLogEntry(String.format("%s attacks %s dealing %s damage.",
                enemy.getName(), player.getPlayerName(), formatDamage(damage)), player.getPlayerId());
        if (player.isDead()) {
            battle.addLogEntry(String.format("%s is dead.", player.getPlayerName()));
        }
    }

    private Object formatDamage(int damage) {
        return damage > 0 ? damage : "no";
    }

    private void attackEnemy(BattleDto battle, StatsDto playerStats, EnemyDto enemyDto) {
        int damage = getDamageToEnemy(playerStats, enemyDto);
        enemyDto.damage(damage);
        battle.addLogEntry(String.format("%s attacks %s dealing %s damage.",
                playerStats.getPlayerName(), enemyDto.getName(), formatDamage(damage)), enemyDto.getId());
        if (enemyDto.isDefeated()) {
            battle.defeatEnemyAndAllotRewardToPlayer(enemyDto, playerStats.getPlayerId());
        }
    }

    private void castSpell(String spellName, String targetId, BattleDto battle, StatsDto playerStats) {
        Spell spell = Spell.valueOf(spellName.toUpperCase());
        if (spell.getMp() > playerStats.getMp()) {
            battle.addLogEntry(String.format("%s tries to cast %s but doesn't have enough MP",
                    playerStats.getPlayerName(), spellName));
        } else {
            playerStats.setMp(playerStats.getMp() - spell.getMp());
            //TODO generalize once more spells exist:
            if (spell.getBattleTarget() == BattleTarget.ENEMY) {
                getTargetedOrFirstEnemy(targetId, battle)
                        .ifPresent(targetedOrFirstEnemy ->
                                castSpellOnEnemy(spellName, battle, playerStats, spell, targetedOrFirstEnemy));
            } else {
                StatsDto playerTarget = battle.getPlayerStats().get(Integer.parseInt(targetId));
                int playerHp = playerTarget.getHp();
                int playerHpTotal = playerTarget.getHpTotal();
                int restoredHp = playerHp + spell.getEffect() > playerHpTotal ?
                        playerHpTotal - playerHp :
                        playerHp + spell.getEffect();
                playerTarget.setHp(restoredHp);
                battle.addLogEntry(String.format("%s casts %s restoring %s HP to %s.",
                        playerStats.getPlayerName(), spellName, restoredHp, playerTarget.getPlayerName()));
            }
        }
    }

    private void castSpellOnEnemy(String spellName, BattleDto battle, StatsDto playerStats, Spell spell, EnemyDto enemyDto) {
        int damage = spell.getEffect();
        enemyDto.damage(damage);
        battle.addLogEntry(String.format("%s casts %s on %s dealing %s damage.",
                playerStats.getPlayerName(), spellName, enemyDto.getName(), formatDamage(damage)));
        if (enemyDto.isDefeated()) {
            battle.defeatEnemyAndAllotRewardToPlayer(enemyDto, playerStats.getPlayerId());
        }
    }

    private Optional<EnemyDto> getTargetedOrFirstEnemy(String targetId, BattleDto battle) {
        List<EnemyDto> livingEnemies = battle.getLivingEnemies();
        UUID enemyId = UUID.fromString(targetId);
        return livingEnemies.stream()
                .filter(enemy -> enemy.getId().equals(enemyId))
                .findFirst()
                .or(() -> livingEnemies.stream()
                        .findFirst());
    }

    private int getDamageToPlayer(EnemyDto enemy, StatsDto player, PlayerAction playerAction) {
        int defense = playerAction.getAction().equals(PARRY) ?
                player.getDefense() * PARRY_DEFENSE_MULTIPLIER :
                player.getDefense();
        return Math.max(enemy.getStats().getAttack() - defense, 0);
    }

    private int getDamageToEnemy(StatsDto playerStats, EnemyDto enemyDto) {
        return playerStats.getAttack() - enemyDto.getStats().getDefense();
    }
}
