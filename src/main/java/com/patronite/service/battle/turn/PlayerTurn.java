package com.patronite.service.battle.turn;

import com.patronite.service.dto.player.StatsDto;

public class PlayerTurn implements Turn {
    private final StatsDto player;

    public PlayerTurn(StatsDto player) {
        this.player = player;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public int getOrder() {
        return player.getAgility();
    }

    public int getPlayerId() {
        return player.getPlayerId();
    }
}
