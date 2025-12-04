package com.patronite.service.assembler;

import com.patronite.service.battle.BattleTarget;
import com.patronite.service.dto.item.ItemDto;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.SpellDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.level.Level;
import com.patronite.service.location.Town;
import com.patronite.service.model.*;
import com.patronite.service.spell.Spell;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PlayerAssembler {
    private PlayerAssembler() {
    }

    public PlayerDto dto(Player player, List<Spell> spells, int xpTillNextLevel) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(player.getId());
        playerDto.setName(player.getName());
        playerDto.setUsername(player.getUsername());
        setStatsDto(playerDto, player, xpTillNextLevel);
        setLocationDto(playerDto, player);
        setSpellDtos(playerDto, spells);
        playerDto.setLastUpdate(new Date());
        playerDto.setVisited(player.getVisited());
        playerDto.setTutorialLessons(player.getTutorialLessons());
        updateDtoInventory(player, playerDto);
        return playerDto;
    }

    public void updateDtoInventory(Player player, PlayerDto playerDto) {
        playerDto.setInventory(player.getInventory().stream().map(item -> {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(item.getId());
            itemDto.setItemDetails(item.getItemDetails());
            itemDto.setEquipped(item.isEquipped());
            return itemDto;
        }).collect(Collectors.toList()));
    }

    public void setSpellDtos(PlayerDto playerDto, List<Spell> spells) {
        playerDto.setSpells(spells.stream()
                .map(spell -> new SpellDto(spell.name(), spell.getMp(), BattleTarget.ENEMY == spell.getBattleTarget(), spell.isBattleSpell()))
                .collect(Collectors.toList()));
    }

    public Player entity(PlayerDto playerDto, Level levelStats) {
        Player player = new Player();
        player.setId(playerDto.getId());
        player.setName(playerDto.getName());
        player.setUsername(playerDto.getUsername());
        player.setPassword(playerDto.getPassword());
        setStats(player, playerDto, levelStats);
        setLocation(player, playerDto);
        return player;
    }

    public static void updatePlayer(Player player, PlayerDto updatedPlayerDto, boolean saveGame) {
        if (updatedPlayerDto.getName() != null) {
            player.setName(updatedPlayerDto.getName());
        }
        if (updatedPlayerDto.getUsername() != null) {
            player.setUsername(updatedPlayerDto.getUsername());
        }
        if (updatedPlayerDto.getPassword() != null) {
            player.setPassword(updatedPlayerDto.getPassword());
        }
        if (updatedPlayerDto.getTutorialLessons() != null) {
            player.setTutorialLessons(updatedPlayerDto.getTutorialLessons());
        }
        updateStats(player, updatedPlayerDto, saveGame);
        updateLocation(player, updatedPlayerDto, saveGame);
        updatePlayerInventory(player, updatedPlayerDto);
    }

    public static void updateStats(Stats stats, StatsDto statsDto, boolean restoreHpMp) {
        stats.setLevel(statsDto.getLevel());
        stats.setXp(statsDto.getXp());
        if (restoreHpMp) {
            statsDto.setHp(statsDto.getHpTotal());
            statsDto.setMp(statsDto.getMpTotal());
        }
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
            locationDto.setEntranceName(location.getEntranceName());
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
            location.setEntranceName(locationDto.getEntranceName());
            player.setLocation(location);
            location.setPlayer(player);
        }
    }

    private static void setStatsDto(PlayerDto playerDto, Player player, int xpTillNextLevel) {
        Stats stats = player.getStats();
        if (stats != null) {
            StatsDto statsDto = new StatsDto();
            statsDto.setPlayerId(stats.getPlayerId());
            statsDto.setPlayerName(player.getName());
            statsDto.setLevel(stats.getLevel());
            statsDto.setXp(stats.getXp());
            statsDto.setXpTillNextLevel(xpTillNextLevel);
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
            updateStats(player, playerDto, false);
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

    private static void updateStats(Player player, PlayerDto playerDto, boolean restoreHpMp) {
        StatsDto statsDto = playerDto.getStats();
        if (statsDto != null) {
            Stats stats = player.getStats();
            updateStats(stats, statsDto, restoreHpMp);
        }
    }

    private static void updateLocation(Player player, PlayerDto playerDto, boolean saveGame) {
        LocationDto locationDto = playerDto.getLocation();
        if (locationDto != null) {
            Location location = player.getLocation();
            if (!location.getMapName().equals(locationDto.getMapName())) {
                location.setEntranceName(location.getMapName());
                locationDto.setEntranceName(location.getMapName());
            }
            location.setMapName(locationDto.getMapName());
            location.setRowIndex(locationDto.getRowIndex());
            location.setColumnIndex(locationDto.getColumnIndex());
            if (saveGame) {
                if (player.getVisited() == null) {
                    player.setVisited(new HashSet<>());
                }
                Arrays.stream(Town.values())
                        .filter(town -> town.name().equalsIgnoreCase(location.getMapName()))
                        .findFirst()
                        .ifPresent(visitedTown -> player.getVisited().add(visitedTown.name()));
                playerDto.setVisited(player.getVisited());
            }
        }
    }

    private static void updatePlayerInventory(Player player, PlayerDto updatedPlayerDto) {
        Map<Integer, Item> playerItems = player.getInventory().stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
        List<Item> newItems = new ArrayList<>();
        Set<Integer> updatedItemIds = new HashSet<>();
        updatedPlayerDto.getInventory().forEach(itemDto -> {
            updatedItemIds.add(itemDto.getId());
            Item playerItem = playerItems.get(itemDto.getId());
            if (playerItem != null) {
                playerItem.setEquipped(itemDto.isEquipped());
            } else {
                //TODO AP ensure hit from picking up item or remove
                Item newItem = new Item();
                newItem.setId(itemDto.getId());
                newItem.setPlayer(player);
                newItem.setItemDetails(itemDto.getItemDetails());
                newItem.setEquipped(itemDto.isEquipped());
                newItems.add(newItem);
            }
        });
        List<Item> itemsToRemove = playerItems.values().stream()
                .filter(item -> !updatedItemIds.contains(item.getId())).collect(Collectors.toList());
        player.getInventory().removeAll(itemsToRemove);
        player.getInventory().addAll(newItems);
    }

    private static void updateSaveInventory(Save save, Player player) {
        Map<Integer, Item> previouslySavedItems = save.getInventory().stream()
                .collect(Collectors.toMap(Item::getSaveOfItemId, item -> item));
        List<Item> newItemsToSave = new ArrayList<>();
        Set<Integer> updatedItemIds = new HashSet<>();
        if (player.getInventory() != null) {
            player.getInventory().forEach(item -> {
                updatedItemIds.add(item.getId());
                Item previouslySavedItem = previouslySavedItems.get(item.getId());
                if (previouslySavedItem != null) {
                    previouslySavedItem.setEquipped(item.isEquipped());
                } else {
                    Item newlySavedItem = new Item();
                    newlySavedItem.setSave(save);
                    newlySavedItem.setItemDetails(item.getItemDetails());
                    newlySavedItem.setEquipped(item.isEquipped());
                    newlySavedItem.setSaveOfItemId(item.getId());
                    newItemsToSave.add(newlySavedItem);
                }
            });
        }
        List<Item> itemsToRemove = previouslySavedItems.values().stream()
                .filter(item -> !updatedItemIds.contains(item.getSaveOfItemId())).collect(Collectors.toList());
        save.getInventory().removeAll(itemsToRemove);
        save.getInventory().addAll(newItemsToSave);
    }

    public static Save save(Save save, Player player) {
        Stats stats = player.getStats();
        save.setPlayerId(player.getId());
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
        Location location = player.getLocation();
        save.setMapName(location.getMapName());
        save.setRowIndex(location.getRowIndex());
        save.setColumnIndex(location.getColumnIndex());
        updateSaveInventory(save, player);
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
        player.getInventory().clear();
        player.getInventory().addAll(
        save.getInventory().stream().map(savedItem -> {
            Item item = new Item();
            item.setPlayer(player);
            item.setItemDetails(savedItem.getItemDetails());
            item.setEquipped(savedItem.isEquipped());
            return item;
        }).collect(Collectors.toList()));
    }
}
