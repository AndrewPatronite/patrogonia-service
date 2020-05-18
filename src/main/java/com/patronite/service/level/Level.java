package com.patronite.service.level;

import com.patronite.service.spell.Spell;

import java.util.List;

public class Level {
    private final int level;
    private final int experienceRequired;
    private final int hpTotal;
    private final int mpTotal;
    private final int attackTotal;
    private final int defenseTotal;
    private final int agilityTotal;
    private final List<Spell> spellsLearned;

    private Level(int level, int experienceRequired, int hpTotal, int mpTotal, int attackTotal, int defenseTotal, int agilityTotal, List<Spell> spellsLearned) {
        this.level = level;
        this.experienceRequired = experienceRequired;
        this.hpTotal = hpTotal;
        this.mpTotal = mpTotal;
        this.attackTotal = attackTotal;
        this.defenseTotal = defenseTotal;
        this.agilityTotal = agilityTotal;
        this.spellsLearned = spellsLearned;
    }

    public static Level newLevel(int level, int experienceRequired, int hpTotal, int mpTotal, int attackTotal, int defenseTotal, int agilityTotal, List<Spell> spellsLearned) {
        return new Level(level, experienceRequired, hpTotal, mpTotal, attackTotal, defenseTotal, agilityTotal, spellsLearned);
    }

    public int getLevel() {
        return level;
    }

    public int getExperienceRequired() {
        return experienceRequired;
    }

    public int getHpTotal() {
        return hpTotal;
    }

    public int getMpTotal() {
        return mpTotal;
    }

    public int getAttackTotal() {
        return attackTotal;
    }

    public int getDefenseTotal() {
        return defenseTotal;
    }

    public int getAgilityTotal() {
        return agilityTotal;
    }

    public List<Spell> getSpellsLearned() {
        return spellsLearned;
    }
}
