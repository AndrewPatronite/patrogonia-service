package com.patronite.service;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.dto.item.ItemDto;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.field.Field;
import com.patronite.service.location.Town;
import com.patronite.service.model.ItemDetails;
import com.patronite.service.model.Player;
import com.patronite.service.model.ShopType;
import com.patronite.service.repository.PlayerRepository;
import com.patronite.service.save.SaveManager;
import com.patronite.service.spell.OutsideDestination;
import com.patronite.service.stats.StatsManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

@Service
public class ItemService {
    private final PlayerRepository playerRepository;
    private final PlayerAssembler playerAssembler;
    private final StatsManager statsManager;
    private final SaveManager saveManager;
    private final Map<Field, Map<ShopType, List<ItemDetails>>> locationToItems;

    public ItemService(PlayerRepository playerRepository, PlayerAssembler playerAssembler, StatsManager statsManager, SaveManager saveManager) {
        this.playerRepository = playerRepository;
        this.playerAssembler = playerAssembler;
        this.statsManager = statsManager;
        this.saveManager = saveManager;
        locationToItems = new ConcurrentHashMap<>();
        Map<ShopType, List<ItemDetails>> dewhurstItems = new ConcurrentHashMap<>();
        dewhurstItems.put(ShopType.GENERAL, asList(ItemDetails.HEALTH_POTION, ItemDetails.DRAGON_WINGS, ItemDetails.ESCAPE_PIPE));
        locationToItems.put(Field.DEWHURST, dewhurstItems);
    }

    public List<ItemDetails> getShopInventory(String mapName, String shopType) {
        return Optional.ofNullable(Optional.ofNullable(locationToItems.get(Field.valueOf(mapName)))
                        .orElseThrow(IllegalArgumentException::new)
                        .get(ShopType.valueOf(shopType)))
                .orElseThrow(IllegalArgumentException::new);
    }

    public PlayerDto purchaseItem(PlayerDto playerDto, String itemName) {
        ItemDetails itemDetails = ItemDetails.valueOf(itemName.toUpperCase().replaceAll(" ", "_"));
        StatsDto playerStats = playerDto.getStats();
        int playerGold = playerStats.getGold();
        int itemCost = itemDetails.getValue();
        if (playerGold >= itemCost) {
            playerStats.setGold(playerGold - itemCost);
            Player player = playerRepository.getOne(playerDto.getId());
            ItemDto item = new ItemDto();
            item.setItemDetails(itemDetails);
            playerDto.getInventory().add(item);
            statsManager.updateStats(player.getId(), playerStats);
            saveManager.save(player, playerDto);
            playerAssembler.updateDtoInventory(player, playerDto);
            return playerDto;
        } else {
            throw new IllegalStateException(String.format("Not enough gold to buy %s", itemDetails.getName()));
        }
    }

    public PlayerDto sellItem(PlayerDto playerDto, Integer itemId) {
        StatsDto playerStats = playerDto.getStats();
        Optional<ItemDto> playerItem = playerDto.getInventory().stream().filter(item -> item.getId() == itemId).findFirst();
        if (playerItem.isPresent()) {
            ItemDto itemToSell = playerItem.get();
            playerStats.setGold(playerStats.getGold() + (int) Math.ceil(itemToSell.getItemDetails().getValue() / 2.0));
            playerDto.getInventory().remove(itemToSell);
            Player player = playerRepository.getOne(playerDto.getId());
            statsManager.updateStats(player.getId(), playerStats);
            saveManager.save(player, playerDto);
            return playerDto;
        } else {
            throw new IllegalStateException(String.format("Player item %s not found", itemId));
        }
    }

    public PlayerDto useItem(PlayerDto playerDto, Integer itemId, Optional<String> targetId) {
        StatsDto playerStats = playerDto.getStats();
        Optional<ItemDto> playerItem = playerDto.getInventory().stream().filter(item -> item.getId() == itemId).findFirst();
        if (playerItem.isPresent()) {
            ItemDto itemToUse = playerItem.get();
            playerDto.getInventory().remove(itemToUse);
            Player player = playerRepository.getOne(playerDto.getId());
            LocationDto location = playerDto.getLocation();

            switch (itemToUse.getItemDetails().getName()) {
                case "Health Potion":
                    int playerHpTotal = playerStats.getHpTotal();
                    int playerHp = playerStats.getHp();
                    int healingEffect = itemToUse.getItemDetails().getEffect();
                    int restoredHp = playerHp + healingEffect > playerHpTotal ?
                            playerHpTotal - playerHp :
                            healingEffect;
                    playerStats.setHp(playerHp + restoredHp);
                    PlayerAssembler.updatePlayer(player, playerDto, false);
                    playerRepository.save(player);
                    break;
                case "Dragon Wings":
                    if (targetId.isEmpty()) {
                        throw new IllegalArgumentException("Destination is null");
                    }
                    Town town = Town.valueOf(targetId.get().toUpperCase());
                    location.setEntranceName(town.getEntranceName());
                    location.setMapName(town.getMapName());
                    location.setRowIndex(town.getRowIndex());
                    location.setColumnIndex(town.getColumnIndex());
                    location.setFacing(town.getTownCenterDirection());
                    saveManager.save(player, playerDto);
                    break;
                case "Escape Pipe":
                    String fieldMapName = location.getEntranceName();
                    OutsideDestination destination = OutsideDestination.valueOf(fieldMapName.toUpperCase());
                    location.setEntranceName(location.getMapName());
                    location.setMapName(fieldMapName);
                    location.setRowIndex(destination.getRowIndex());
                    location.setColumnIndex(destination.getColumnIndex());
                    location.setFacing("down");
                    PlayerAssembler.updatePlayer(player, playerDto, false);
                    playerRepository.save(player);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unsupported item %s", itemToUse.getItemDetails().getName()));
            }
        } else {
            throw new IllegalStateException(String.format("Player item %s not found", itemId));
        }
        return playerDto;
    }
}
