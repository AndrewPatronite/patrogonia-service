package com.patronite.service.battle.turn;

import com.patronite.service.battle.BattleStatus;
import com.patronite.service.battle.CombatManager;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.player.StatsDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class RoundManager {
    private final CombatManager combatManager;

    public RoundManager(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    public BattleStatus conductRound(BattleDto battle) {
        battle.startRound();
        getTurnList(battle).stream()
                .takeWhile(turn -> BattleStatus.IN_PROGRESS.equals(battle.getStatus()))
                .forEach(turn -> takeTurn(battle, turn));
        return battle.getStatus();
    }

    private List<Turn> getTurnList(BattleDto battle) {
        List<Turn> turnList = newArrayList();
        battle.getLivingEnemies().stream()
                .sorted(Comparator.comparingInt(enemy -> enemy.getStats().getAgility()))
                .forEach(enemyDto -> turnList.add(new EnemyTurn(enemyDto)));
        battle.getPlayerStats().values().stream()
                .sorted(Comparator.comparingInt(StatsDto::getAgility)).forEach(
                playerStats -> turnList.add(new PlayerTurn(playerStats)));
        turnList.sort(Comparator.comparingInt(Turn::getOrder));
        Collections.reverse(turnList);
        return turnList;
    }

    private void takeTurn(BattleDto battle, Turn turn) {
        if (turn.isPlayer()) {
            combatManager.fightEnemy(battle, ((PlayerTurn) turn).getPlayerId());
        } else {
            combatManager.fightPlayer(battle, ((EnemyTurn) turn).getEnemyId());
        }
        if (battle.getLivingPlayers().isEmpty()) {
            battle.addLogEntry("The party was destroyed!");
            battle.setStatus(BattleStatus.DEFEAT);
        } else if (battle.getLivingEnemies().isEmpty()) {
            battle.addLogEntry("The enemies are defeated!");
            battle.setStatus(BattleStatus.VICTORY);
        }
    }
}
