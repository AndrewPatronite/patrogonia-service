package com.patronite.service.dto.enemy;

import com.patronite.service.dto.item.RewardDto;
import com.patronite.service.dto.player.StatsDto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class EnemyDto implements Serializable {
    private static final long serialVersionUID = 3469492096495412230L;
    private UUID id;
    private String name;
    private StatsDto stats;
    private List<String> spells;
    private RewardDto rewardDto;

    public EnemyDto(String name, StatsDto stats, List<String> spells, RewardDto rewardDto) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.stats = stats;
        this.spells = spells;
        this.rewardDto = rewardDto;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StatsDto getStats() {
        return stats;
    }

    public List<String> getSpells() {
        return spells;
    }

    public RewardDto getRewardDto() {
        return rewardDto;
    }

    public void damage(int damage) {
        int hp = stats.getHp();
        stats.setHp(damage > hp ? 0 : hp - damage);
    }

    public boolean isDefeated() {
        return stats.getHp() <= 0;
    }
}
