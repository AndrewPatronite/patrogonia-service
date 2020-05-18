package com.patronite.service.battle.enemy;

import com.patronite.service.dto.item.RewardDto;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public enum Enemy {
    MOUSE("Mouse", 3, 0, 5, 2, 5, newArrayList(), new RewardDto(1, 1, newHashSet())),
    RAT("Rat", 7, 0, 6, 3, 7, newArrayList(), new RewardDto(2, 2, newHashSet())),
    GOBLIN("Goblin", 12, 0, 12, 6, 12, newArrayList(), new RewardDto(4, 4, newHashSet())),
    BOAR("Boar", 15, 0, 15, 10, 15, newArrayList(), new RewardDto(3, 6, newHashSet())),
    SKELETON("Skeleton", 20, 0, 20, 15, 20, newArrayList(), new RewardDto(5, 10, newHashSet())),
    KNIGHT("Knight", 25, 0, 25, 20, 10, newArrayList(), new RewardDto(20, 15, newHashSet()));

    private final String name;
    private final int hp;
    private final int mp;
    private final int attack;
    private final int defense;
    private final int agility;
    private final List<String> spells;
    private final RewardDto rewardDto;

    Enemy(String name, int hp, int mp, int attack, int defense, int agility, List<String> spells, RewardDto rewardDto) {
        this.name = name;
        this.hp = hp;
        this.mp = mp;
        this.attack = attack;
        this.defense = defense;
        this.agility = agility;
        this.spells = spells;
        this.rewardDto = rewardDto;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getAgility() {
        return agility;
    }

    public List<String> getSpells() {
        return spells;
    }

    public RewardDto getRewardDto() {
        return rewardDto;
    }

    public static Enemy getEnemy(String enemyName) {
        return Enemy.valueOf(enemyName);
    }
}
