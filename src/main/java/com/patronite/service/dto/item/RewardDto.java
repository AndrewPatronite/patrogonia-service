package com.patronite.service.dto.item;

import java.io.Serializable;
import java.util.Collection;

public class RewardDto implements Serializable {
    private static final long serialVersionUID = 5797484052651279084L;
    private int gold;
    private int experience;
    private Collection<ItemDto> items;

    public RewardDto(int gold, int experience, Collection<ItemDto> items) {
        this.gold = gold;
        this.experience = experience;
        this.items = items;
    }

    public int getGold() {
        return gold;
    }

    public int getExperience() {
        return experience;
    }

    public Collection<ItemDto> getItems() {
        return items;
    }
}
