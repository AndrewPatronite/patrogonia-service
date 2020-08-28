package com.patronite.service.level;

import com.google.common.collect.Maps;
import com.patronite.service.spell.Spell;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.patronite.service.level.Level.newLevel;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Component
public class Levels {
    public static final int MAX_LEVEL = 10;
    private final Map<Integer, Level> levelMap;

    public Levels() {
        levelMap = Maps.newHashMap();
        levelMap.put(1, newLevel(1, 0, 12, 5, 5, 4, 5, emptyList()));
        levelMap.put(2, newLevel(2, 7, 18, 12, 7, 5, 6, singletonList(Spell.HEAL)));
        levelMap.put(3, newLevel(3, 14, 30, 17, 10, 6, 7, singletonList(Spell.OUTSIDE)));
        levelMap.put(4, newLevel(4, 28, 42, 24, 16, 8, 10, singletonList(Spell.RETURN)));
        levelMap.put(5, newLevel(5, 56, 54, 27, 20, 10, 12, emptyList()));
        levelMap.put(6, newLevel(6, 112, 66, 35, 23, 12, 14, emptyList()));
        levelMap.put(7, newLevel(7, 224, 88, 42, 26, 14, 16, singletonList(Spell.ICE)));
        levelMap.put(8, newLevel(8, 448, 100, 50, 29, 16, 18, emptyList()));
        levelMap.put(9, newLevel(9, 896, 112, 57, 32, 18, 20, emptyList()));
        levelMap.put(MAX_LEVEL, newLevel(MAX_LEVEL, 1972, 125, 60, 35, 20, 21, emptyList()));
    }

    public Map<Integer, Level> levelMap() {
        return levelMap;
    }
}
