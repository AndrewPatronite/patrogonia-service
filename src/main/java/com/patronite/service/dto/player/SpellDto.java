package com.patronite.service.dto.player;

import java.io.Serializable;

public class SpellDto implements Serializable {
    private static final long serialVersionUID = -1307884132648940218L;
    private String spellName;
    private int mpCost;
    private boolean offensive;

    public SpellDto(String spellName, int mpCost, boolean offensive) {
        this.spellName = spellName;
        this.mpCost = mpCost;
        this.offensive = offensive;
    }

    public String getSpellName() {
        return spellName;
    }

    public int getMpCost() {
        return mpCost;
    }

    public boolean isOffensive() {
        return offensive;
    }
}
