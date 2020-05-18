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
        levelMap.put(1, newLevel(1, 0, 12, 1, 5, 4, 5, emptyList()));
        levelMap.put(2, newLevel(2, 15, 18, 3, 7, 5, 6, emptyList()));
        levelMap.put(3, newLevel(3, 30, 30, 5, 10, 6, 7, singletonList(Spell.HEAL)));
        levelMap.put(4, newLevel(4, 60, 42, 8, 16, 8, 10, emptyList()));
        levelMap.put(5, newLevel(5, 120, 54, 10, 20, 10, 12, emptyList()));
        levelMap.put(6, newLevel(6, 240, 66, 12, 23, 12, 14, emptyList()));
        levelMap.put(7, newLevel(7, 480, 88, 15, 26, 14, 16, singletonList(Spell.ICE)));
        levelMap.put(8, newLevel(8, 960, 100, 20, 29, 16, 18, emptyList()));
        levelMap.put(9, newLevel(9, 1920, 112, 25, 32, 18, 20, emptyList()));
        levelMap.put(MAX_LEVEL, newLevel(MAX_LEVEL, 3840, 125, 30, 35, 20, 21, emptyList()));
    }

    public Map<Integer, Level> levelMap() {
        return levelMap;
    }
}
