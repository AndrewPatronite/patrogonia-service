package com.patronite.service.dto.player;

import java.io.Serializable;

public class SpellDto implements Serializable {
    private static final long serialVersionUID = -8303537884747838958L;
    private String spellName;
    private int mpCost;
    private boolean offensive;
    private boolean isBattleSpell;

    public SpellDto() {
    }

    public SpellDto(String spellName, int mpCost, boolean offensive, boolean isBattleSpell) {
        this.spellName = spellName;
        this.mpCost = mpCost;
        this.offensive = offensive;
        this.isBattleSpell = isBattleSpell;
    }

    public String getSpellName() {
        return spellName;
    }

    public void setSpellName(String spellName) {
        this.spellName = spellName;
    }

    public int getMpCost() {
        return mpCost;
    }

    public void setMpCost(int mpCost) {
        this.mpCost = mpCost;
    }

    public boolean isOffensive() {
        return offensive;
    }

    public void setOffensive(boolean offensive) {
        this.offensive = offensive;
    }

    public boolean isBattleSpell() {
        return isBattleSpell;
    }

    public void setBattleSpell(boolean battleSpell) {
        isBattleSpell = battleSpell;
    }
}
