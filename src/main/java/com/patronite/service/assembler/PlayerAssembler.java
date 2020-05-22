package com.patronite.service.assembler;

import com.patronite.service.battle.BattleTarget;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.SpellDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.level.Level;
import com.patronite.service.model.Location;
import com.patronite.service.model.Player;
import com.patronite.service.model.Save;
import com.patronite.service.model.Stats;
import com.patronite.service.spell.Spell;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerAssembler {
    private PlayerAssembler() {
    }

    public static PlayerDto dto(Player player, List<Spell> spells) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(player.getId());
        playerDto.setName(player.getName());
        playerDto.setUsername(player.getUsername());
        setStatsDto(playerDto, player);
        setLocationDto(playerDto, player);
        setSpellDtos(playerDto, spells);
        playerDto.setLastUpdate(new Date());
        return playerDto;
    }

    private static void setSpellDtos(PlayerDto playerDto, List<Spell> spells) {
        playerDto.setSpells(spells.stream()
                .map(spell -> new SpellDto(spell.name(), spell.getMp(), BattleTarget.ENEMY == spell.getBattleTarget()))
                .collect(Collectors.toList()));
    }

    public static Player entity(PlayerDto playerDto, Level levelStats) {
        Player player = new Player();
        player.setId(playerDto.getId());
        player.setName(playerDto.getName());
        player.setUsername(playerDto.getUsername());
        //TODO encrypt!
        player.setPassword(playerDto.getPassword());
        setStats(player, playerDto, levelStats);
        setLocation(player, playerDto);
        return player;
    }

    public static void updatePlayer(Player player, PlayerDto updatedPlayerDto) {
        if (updatedPlayerDto.getName() != null) {
            player.setName(updatedPlayerDto.getName());
        }
        if (updatedPlayerDto.getUsername() != null) {
            player.setUsername(updatedPlayerDto.getUsername());
        }
        if (updatedPlayerDto.getPassword() != null) {
            //TODO encrypt!
            player.setPassword(updatedPlayerDto.getPassword());
        }
        updateStats(player, updatedPlayerDto);
        updateLocation(player, updatedPlayerDto);
    }

    public static void updateStats(Stats stats, StatsDto statsDto) {
        stats.setLevel(statsDto.getLevel());
        stats.setXp(statsDto.getXp());
        stats.setHp(statsDto.getHp());
        stats.setHpTotal(statsDto.getHpTotal());
        stats.setMp(statsDto.getMp());
        stats.setMpTotal(statsDto.getMpTotal());
        stats.setAttack(statsDto.getAttack());
        stats.setDefense(statsDto.getDefense());
        stats.setAgility(statsDto.getAgility());
        stats.setGold(statsDto.getGold());
    }

    private static void setLocationDto(PlayerDto playerDto, Player player) {
        Location location = player.getLocation();
        if (location != null) {
            LocationDto locationDto = new LocationDto();
            locationDto.setMapName(location.getMapName());
            locationDto.setRowIndex(location.getRowIndex());
            locationDto.setColumnIndex(location.getColumnIndex());
            playerDto.setLocation(locationDto);
        }
    }

    private static void setLocation(Player player, PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocation();
        if (locationDto != null) {
            Location location = new Location();
            location.setPlayerId(playerDto.getId());
            location.setMapName(locationDto.getMapName());
            location.setRowIndex(locationDto.getRowIndex());
            location.setColumnIndex(locationDto.getColumnIndex());
            player.setLocation(location);
            location.setPlayer(player);
        }
    }

    private static void setStatsDto(PlayerDto playerDto, Player player) {
        Stats stats = player.getStats();
        if (stats != null) {
            StatsDto statsDto = new StatsDto();
            statsDto.setPlayerId(stats.getPlayerId());
            statsDto.setPlayerName(player.getName());
            statsDto.setLevel(stats.getLevel());
            statsDto.setXp(stats.getXp());
            statsDto.setHp(stats.getHp());
            statsDto.setHpTotal(stats.getHpTotal());
            statsDto.setMp(stats.getMp());
            statsDto.setMpTotal(stats.getMpTotal());
            statsDto.setAttack(stats.getAttack());
            statsDto.setDefense(stats.getDefense());
            statsDto.setAgility(stats.getAgility());
            statsDto.setGold(stats.getGold());
            playerDto.setStats(statsDto);
        }
    }

    private static void setStats(Player player, PlayerDto playerDto, Level levelStats) {
        StatsDto statsDto = playerDto.getStats();
        if (statsDto != null) {
            Stats stats = new Stats();
            player.setStats(stats);
            stats.setPlayer(player);
            updateStats(player, playerDto);
        } else {
            Stats stats = player.getStats() != null ?  player.getStats() : new Stats();
            stats.setLevel(levelStats.getLevel());
            stats.setXp(levelStats.getExperienceRequired());
            stats.setHp(levelStats.getHpTotal());
            stats.setHpTotal(levelStats.getHpTotal());
            stats.setMp(levelStats.getMpTotal());
            stats.setMpTotal(levelStats.getMpTotal());
            stats.setAttack(levelStats.getAttackTotal());
            stats.setDefense(levelStats.getDefenseTotal());
            stats.setAgility(levelStats.getAgilityTotal());
            player.setStats(stats);
            stats.setPlayer(player);
        }
    }

    private static void updateStats(Player player, PlayerDto playerDto) {
        StatsDto statsDto = playerDto.getStats();
        if (statsDto != null) {
            Stats stats = player.getStats();
            updateStats(stats, statsDto);
        }
    }

    private static void updateLocation(Player player, PlayerDto playerDto) {
        LocationDto locationDto = playerDto.getLocation();
        if (locationDto != null) {
            Location location = player.getLocation();
            location.setMapName(locationDto.getMapName());
            location.setRowIndex(locationDto.getRowIndex());
            location.setColumnIndex(locationDto.getColumnIndex());
        }
    }

    public static Save save(PlayerDto updatedPlayerDto) {
        Save save = new Save();
        StatsDto stats = updatedPlayerDto.getStats();
        save.setPlayerId(updatedPlayerDto.getId());
        save.setLevel(stats.getLevel());
        save.setXp(stats.getXp());
        save.setHp(stats.getHpTotal());
        stats.setHp(stats.getHpTotal());
        save.setHpTotal(stats.getHpTotal());
        save.setMp(stats.getMpTotal());
        stats.setMp(stats.getMpTotal());
        save.setMpTotal(stats.getMpTotal());
        save.setAttack(stats.getAttack());
        save.setDefense(stats.getDefense());
        save.setAgility(stats.getAgility());
        save.setGold(stats.getGold());
        LocationDto location = updatedPlayerDto.getLocation();
        save.setMapName(location.getMapName());
        save.setRowIndex(location.getRowIndex());
        save.setColumnIndex(location.getColumnIndex());
        return save;
    }

    public static void loadSave(Player player, Save save) {
        Stats stats = player.getStats();
        stats.setLevel(save.getLevel());
        stats.setXp(save.getXp());
        stats.setHp(save.getHp());
        stats.setHpTotal(save.getHpTotal());
        stats.setMp(save.getMp());
        stats.setMpTotal(save.getMpTotal());
        stats.setAttack(save.getAttack());
        stats.setDefense(save.getDefense());
        stats.setAgility(save.getAgility());
        stats.setGold(save.getGold());
        Location location = player.getLocation();
        location.setMapName(save.getMapName());
        location.setRowIndex(save.getRowIndex());
        location.setColumnIndex(save.getColumnIndex());
    }
}
