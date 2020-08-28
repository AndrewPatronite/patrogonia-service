package com.patronite.service.spell;

import com.patronite.service.battle.BattleTarget;

public enum Spell {
    HEAL(5, 20, BattleTarget.PLAYER, true),
    ICE(5, 20, BattleTarget.ENEMY, true),
    OUTSIDE(7, 0, BattleTarget.PLAYER, false),
    RETURN(7, 0, BattleTarget.PLAYER, false);

    private final int mp;
    private final int effect;
    private final BattleTarget battleTarget;
    private final boolean isBattleSpell;

    Spell(int mp, int effect, BattleTarget battleTarget, boolean isBattleSpell) {
        this.mp = mp;
        this.effect = effect;
        this.battleTarget = battleTarget;
        this.isBattleSpell = isBattleSpell;
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

    public boolean isBattleSpell() {
        return isBattleSpell;
    }
}
