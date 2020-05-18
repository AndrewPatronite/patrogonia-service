package com.patronite.service.dto.player;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class PlayerDto implements Serializable {
    private static final long serialVersionUID = 4907150542890149455L;
    private int id;
    private String name;
    private String username;
    private String password;
    private LocationDto location;
    private StatsDto stats;
    private String battleId;
    private boolean saveGame;
    private List<SpellDto> spells = newArrayList();

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

    public boolean isSaveGame() {
        return saveGame;
    }

    public void setSaveGame(boolean saveGame) {
        this.saveGame = saveGame;
    }

    public List<SpellDto> getSpells() {
        return spells;
    }

    public void setSpells(List<SpellDto> spells) {
        this.spells = spells;
    }

    @Override
    public String toString() {
        return "PlayerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", location=" + location +
                ", statsDto=" + stats +
                ", battleId='" + battleId + '\'' +
                ", saveGame=" + saveGame +
                '}';
    }
}
