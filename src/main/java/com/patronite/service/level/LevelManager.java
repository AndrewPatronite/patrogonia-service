package com.patronite.service.level;

import com.patronite.service.spell.Spell;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class LevelManager {
    private final Levels levels;

    public LevelManager(Levels levels) {
        this.levels = levels;
    }

    public List<Spell> getSpells(int level) {
        List<Spell> spells = newArrayList();
        levels.levelMap().values().stream()
                .filter(levelEntry -> levelEntry.getLevel() <= level)
                .forEach(levelEntry -> spells.addAll(levelEntry.getSpellsLearned()));
        return spells;
    }

    public Level getLevel(int level) {
        return levels.levelMap().get(level);
    }

    public Optional<Level> promoteToHigherLevel(int currentLevel, int currentExperience) {
        Optional<Level> levelPromotion = Optional.empty();
        for (int i = currentLevel + 1; i <= Levels.MAX_LEVEL; i++) {
            Level higherLevel = getLevel(i);
            if (currentExperience >= higherLevel.getExperienceRequired()) {
                levelPromotion = Optional.of(higherLevel);
            } else {
                break;
            }
        }
        return levelPromotion;
    }

    public int getXpTillNextLevel(int playerLevel, int xp) {
        Map<Integer, Level> levelMap = levels.levelMap();
        Level nextLevel = levelMap.get(playerLevel + 1);
        return nextLevel != null ?
                nextLevel.getExperienceRequired() - xp :
                0;
    }
}
