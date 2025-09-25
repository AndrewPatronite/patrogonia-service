package com.patronite.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ItemDetails {
    HEALTH_POTION("Health Potion", ItemCategory.CONSUMABLE, "Recovers some hit points", 10, 5),
    DRAGON_WINGS("Dragon Wings", ItemCategory.CONSUMABLE, "Returns player to a village", 0, 10),
    ESCAPE_PIPE("Escape Pipe", ItemCategory.CONSUMABLE, "Exits a dungeon", 0, 10);

    private final String name;
    private final ItemCategory category;
    private final String description;
    private final int effect;
    private final int value;

    ItemDetails(String name, ItemCategory category, String description, int effect, int value) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.effect = effect;
        this.value = value;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ItemDetails fromJson(@JsonProperty("name") String name) {
        return valueOf(name.toUpperCase().replaceAll(" ", "_"));
    }

    public ItemCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getEffect() {
        return effect;
    }

    public int getValue() {
        return value;
    }
}
