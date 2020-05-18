package com.patronite.service.battle.enemy;

import com.patronite.service.dto.enemy.EnemyDto;
import com.patronite.service.dto.player.StatsDto;

public class EnemyFactory {
    private EnemyFactory() {
    }

    public static EnemyDto create(Enemy enemy) {
        StatsDto stats = new StatsDto();
        stats.setHpTotal(enemy.getHp());
        stats.setHp(enemy.getHp());
        stats.setMp(enemy.getMp());
        stats.setMpTotal(enemy.getMp());
        stats.setAttack(enemy.getAttack());
        stats.setDefense(enemy.getDefense());
        stats.setAgility(enemy.getAgility());
        return new EnemyDto(enemy.getName(), stats, enemy.getSpells(), enemy.getRewardDto());
    }
}
