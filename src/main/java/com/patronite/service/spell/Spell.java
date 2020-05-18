package com.patronite.service.spell;

import com.patronite.service.battle.BattleTarget;

public enum Spell {
    HEAL(5, 20, BattleTarget.PLAYER),
    ICE(5, 20, BattleTarget.ENEMY);

    private final int mp;
    private final int effect;
    private final BattleTarget battleTarget;

    Spell(int mp, int effect, BattleTarget battleTarget) {
        this.mp = mp;
        this.effect = effect;
        this.battleTarget = battleTarget;
    }

    public int getMp() {
        return mp;
    }

    public int getEffect() {
        return effect;
    }

    public BattleTarget getBattleTarget() {
        return battleTarget;
    }
}
