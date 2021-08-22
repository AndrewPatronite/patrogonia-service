package com.patronite.service.battle.enemy;

import com.patronite.service.dto.enemy.EnemyDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
public class EnemyManager {
    private final Map<String, List<Enemy>> locationToEnemies;

    public EnemyManager() {
        locationToEnemies = new ConcurrentHashMap<>();
        locationToEnemies.put("Atoris", singletonList(Enemy.MOUSE));
        locationToEnemies.put("Lava Grotto", asList(Enemy.MOUSE, Enemy.RAT, Enemy.GOBLIN));
        locationToEnemies.put("Grimes", asList(Enemy.MOUSE, Enemy.RAT, Enemy.BOAR, Enemy.SKELETON, Enemy.KNIGHT));
    }

    public EnemyDto generateEnemy(String mapName) {
        List<Enemy> enemies = locationToEnemies.get(mapName);
        return EnemyFactory.create(enemies.get(new Random().nextInt(enemies.size())));
    }
}
