package com.patronite.service;

import com.patronite.service.battle.BattleManager;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.player.StatsDto;
import org.springframework.stereotype.Service;

@Service
public class BattleService {
    private final BattleManager battleManager;

    public BattleService(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    public void takeTurn(String battleId, int playerId, String playerAction, String targetId) {
        battleManager.takeTurn(battleId, playerId, playerAction, targetId);
    }

    public BattleDto getBattle(String battleId) {
        return battleManager.getBattle(battleId);
    }
}
