package com.patronite.service.battle;

import com.patronite.service.battle.enemy.EnemyManager;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.enemy.EnemyDto;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.model.Location;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class BattleGenerator {
    private static final int BATTLE_PERCENTAGE_UPPER_THRESHOLD = 100;
    private static final int BATTLE_PERCENTAGE_LOWER_THRESHOLD = 25;
    private static final int MIN_FREE_MOVES = 7;
    private static final int MIN_ENEMIES = 1;
    private static final int MAX_ENEMIES = 5;
    private final EnemyManager enemyManager;
    private final Random spawnRandomizer = new Random();
    private final Map<Integer, Integer> movesSinceLastBattle = new ConcurrentHashMap<>();

    public BattleGenerator(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
    }

    public Optional<BattleDto> spawnOrDontSpawnBattle(PlayerDto playerDto, Location lastLocation) {
        Optional<BattleDto> potentialBattle = Optional.empty();
        LocationDto newLocation = playerDto.getLocation();

        if (isDifferentLocationInTheSameMap(lastLocation, newLocation) && shouldSpawn(playerDto)) {
            BattleDto battle = new BattleDto(generateEnemies(newLocation.getMapName()), playerDto);
            potentialBattle = Optional.of(battle);
        }
        return potentialBattle;
    }

    private boolean isDifferentLocationInTheSameMap(Location lastLocation, LocationDto newLocation) {
        return lastLocation.getMapName().equals(newLocation.getMapName()) &&
                (lastLocation.getRowIndex() != newLocation.getRowIndex() ||
                        lastLocation.getColumnIndex() != newLocation.getColumnIndex());
    }

    private boolean shouldSpawn(PlayerDto playerDto) {
        int playerMovesSinceLastBattle = movesSinceLastBattle.getOrDefault(playerDto.getId(), 0) + 1;
        boolean doSpawn = playerMovesSinceLastBattle > MIN_FREE_MOVES &&
                spawnRandomizer.nextInt(BATTLE_PERCENTAGE_UPPER_THRESHOLD + 1) < BATTLE_PERCENTAGE_LOWER_THRESHOLD;
        movesSinceLastBattle.put(playerDto.getId(), doSpawn ? 0 : playerMovesSinceLastBattle);
        return doSpawn;
    }

    private List<EnemyDto> generateEnemies(String mapName) {
        List<EnemyDto> enemies = newArrayList();
        int numberOfEnemies = new Random().ints(1, MIN_ENEMIES, MAX_ENEMIES + 1).sum();
        while (enemies.size() < numberOfEnemies) {
            enemies.add(enemyManager.generateEnemy(mapName));
        }
        return enemies;
    }
}
