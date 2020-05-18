package com.patronite.service.dto.item;

import java.io.Serializable;

public class ItemDto implements Serializable {
    private static final long serialVersionUID = -3351348523503637031L;
    private String name;
    private int goldValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(int goldValue) {
        this.goldValue = goldValue;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    private ItemType itemType;
}
