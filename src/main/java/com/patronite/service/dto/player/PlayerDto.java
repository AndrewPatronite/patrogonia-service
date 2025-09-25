package com.patronite.service.dto.player;

import com.patronite.service.dto.item.ItemDto;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class PlayerDto implements Serializable {
    private static final long serialVersionUID = 7264181887268601842L;
    private int id;
    private String name;
    private String username;
    private String password;
    private LocationDto location;
    private StatsDto stats;
    private String battleId;
    private List<SpellDto> spells = newArrayList();
    private Date lastUpdate = new Date();
    private Set<String> visited = new HashSet<>();
    private Set<String> tutorialLessons = new HashSet<>();
    private List<ItemDto> inventory = newArrayList();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public StatsDto getStats() {
        return stats;
    }

    public void setStats(StatsDto stats) {
        this.stats = stats;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public List<SpellDto> getSpells() {
        return spells;
    }

    public void setSpells(List<SpellDto> spells) {
        this.spells = spells;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<String> getVisited() {
        return visited;
    }

    public void setVisited(Set<String> visited) {
        this.visited = visited;
    }

    public Set<String> getTutorialLessons() {
        return tutorialLessons;
    }

    public void setTutorialLessons(Set<String> tutorialLessons) {
        this.tutorialLessons = tutorialLessons;
    }

    public List<ItemDto> getInventory() {
        return inventory;
    }

    public void setInventory(List<ItemDto> inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "PlayerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", location=" + location +
                ", stats=" + stats +
                ", battleId='" + battleId + '\'' +
                ", spells=" + spells +
                ", lastUpdate=" + lastUpdate +
                ", visited=" + visited +
                ", tutorialLessons=" + tutorialLessons +
                ", inventory=" + inventory +
                '}';
    }
}
