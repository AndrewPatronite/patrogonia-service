package com.patronite.service.battle.turn;

import com.patronite.service.dto.enemy.EnemyDto;

import java.util.UUID;

public class EnemyTurn implements Turn {
    private final EnemyDto enemy;

    public EnemyTurn(EnemyDto enemy) {
        this.enemy = enemy;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public int getOrder() {
        return enemy.getStats().getAgility();
    }

    public UUID getEnemyId() {
        return enemy.getId();
    }
}
