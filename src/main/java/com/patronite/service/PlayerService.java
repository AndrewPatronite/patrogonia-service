package com.patronite.service;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.battle.BattleManager;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.level.Level;
import com.patronite.service.level.LevelManager;
import com.patronite.service.location.Town;
import com.patronite.service.message.PlayerMessenger;
import com.patronite.service.model.Player;
import com.patronite.service.repository.PlayerRepository;
import com.patronite.service.save.SaveManager;
import com.patronite.service.spell.OutsideDestination;
import com.patronite.service.spell.Spell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.patronite.service.spell.Spell.*;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerMessenger playerMessenger;
    private final BattleManager battleManager;
    private final LevelManager levelManager;
    private final SaveManager saveManager;
    private final PlayerAssembler playerAssembler;
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public PlayerService(PlayerRepository playerRepository, PlayerMessenger playerMessenger, BattleManager battleManager, LevelManager levelManager, SaveManager saveManager, PlayerAssembler playerAssembler) {
        this.playerRepository = playerRepository;
        this.playerMessenger = playerMessenger;
        this.battleManager = battleManager;
        this.levelManager = levelManager;
        this.saveManager = saveManager;
        this.playerAssembler = playerAssembler;
    }

    public int create(PlayerDto playerDto) {
        String username = playerDto.getUsername();
        if (playerRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException(String.format("Player with username %s already exists", username));
        }
        Level levelStats = levelManager.getLevel(1);
        Player player = playerRepository.save(playerAssembler.entity(playerDto, levelStats));
        saveManager.save(getPlayer(player.getId()));
        return player.getId();
    }

    public int login(String username, String password) {
        Player player = playerRepository.findByLogin(username, password);
        if (player != null && player.getPassword().equals(password)) {
            return player.getId();
        } else {
            throw new BadCredentialsException("Login failed");
        }
    }

    public PlayerDto getPlayer(int playerId) {
        Player player = playerRepository.getOne(playerId);
        List<Spell> spells = levelManager.getSpells(player.getStats().getLevel());
        int xpTillNextLevel = levelManager.getXpTillNextLevel(player.getStats().getLevel(), player.getStats().getXp());
        return playerAssembler.dto(player, spells, xpTillNextLevel);
    }

    public PlayerDto update(PlayerDto updatedPlayerDto, boolean saveGame) {
        Player player = playerRepository.getOne(updatedPlayerDto.getId());
        if (saveGame) {
            saveManager.save(updatedPlayerDto);
        } else {
            if (Arrays.stream(Town.values())
                    .anyMatch(town -> town.name().equalsIgnoreCase(updatedPlayerDto.getLocation().getMapName()))) {
                //Player in town
            } else if (updatedPlayerDto.getBattleId() == null) {
                battleManager.findBattleInProgressAtLocation(updatedPlayerDto.getLocation())
                        .ifPresentOrElse(battle -> {
                                    battleManager.joinBattle(updatedPlayerDto.getStats(), battle.getId());
                                    updatedPlayerDto.setBattleId(battle.getId().toString());
                                },
                                () -> battleManager.spawnOrDontSpawnBattle(updatedPlayerDto, player.getLocation())
                                        .ifPresent(battle -> updatedPlayerDto.setBattleId(battle.getId().toString())));
            }
        }
        playerAssembler.updatePlayer(player, updatedPlayerDto, saveGame);
        playerRepository.save(player);
        int playerLevel = player.getStats().getLevel();
        List<Spell> spells = levelManager.getSpells(playerLevel);
        playerAssembler.setSpellDtos(updatedPlayerDto, spells);
        updatedPlayerDto.getStats().setXpTillNextLevel(
                levelManager.getXpTillNextLevel(playerLevel, updatedPlayerDto.getStats().getXp()));
        updatedPlayerDto.setLastUpdate(new Date());
        playerMessenger.publishPlayerMessage(updatedPlayerDto);
        logger.info("Updated player {}", updatedPlayerDto);
        return updatedPlayerDto;
    }

    public List<PlayerDto> getPlayers(String mapName) {
        List<Player> players = playerRepository.findByLocation(mapName);
        return players.stream().map(player -> {
            List<Spell> spells = levelManager.getSpells(player.getStats().getLevel());
            int xpTillNextLevel = levelManager.getXpTillNextLevel(player.getStats().getLevel(), player.getStats().getXp());
            PlayerDto playerDto = playerAssembler.dto(player, spells, xpTillNextLevel);
            battleManager.getPlayerBattleId(player.getId())
                    .ifPresent(battleId -> playerDto.setBattleId(battleId.toString()));
            return playerDto;
        }).collect(Collectors.toList());
    }

    public void loadLastSave(int playerId) {
        battleManager.removePlayerFromBattle(playerId);
        saveManager.loadLastSave(playerId);
    }

    public PlayerDto castSpell(PlayerDto playerDto, String spellName, String targetId) {
        StatsDto playerStats = playerDto.getStats();
        int mp = playerStats.getMp();

        playerDto.getSpells().stream()
                .filter(spellDto -> spellDto.getSpellName().equals(spellName))
                .findFirst().
                ifPresent(
                        spell -> {
                            switch (Spell.valueOf(spell.getSpellName())) {
                                case HEAL:
                                    if (Integer.toString(playerDto.getId()).equals(targetId)) {
                                        if (mp >= HEAL.getMp()) {
                                            int playerHpTotal = playerStats.getHpTotal();
                                            int playerHp = playerStats.getHp();
                                            int restoredHp = playerHp + HEAL.getEffect() > playerHpTotal ?
                                                    playerHpTotal - playerHp :
                                                    HEAL.getEffect();
                                            playerStats.setHp(playerHp + restoredHp);
                                            playerStats.setMp(mp - HEAL.getMp());
                                        } else {
                                            throw new IllegalStateException(String.format("Not enough MP to cast %s", spell.getSpellName()));
                                        }
                                    }
                                    break;
                                case OUTSIDE:
                                    if (mp >= OUTSIDE.getMp()) {
                                        Player player = playerRepository.getOne(playerDto.getId());
                                        OutsideDestination destination = OutsideDestination.valueOf(targetId.toUpperCase());
                                        LocationDto location = playerDto.getLocation();
                                        location.setEntranceName(location.getMapName());
                                        location.setMapName(targetId);
                                        location.setRowIndex(destination.getRowIndex());
                                        location.setColumnIndex(destination.getColumnIndex());
                                        playerAssembler.updatePlayer(player, playerDto, false);
                                        playerRepository.save(player);
                                        playerStats.setMp(mp - OUTSIDE.getMp());
                                    } else {
                                        throw new IllegalStateException(String.format("Not enough MP to cast %s", spell.getSpellName()));
                                    }
                                    break;
                                case RETURN:
                                    if (mp >= RETURN.getMp()) {
                                        Player player = playerRepository.getOne(playerDto.getId());
                                        Town town = Town.valueOf(targetId.toUpperCase());
                                        LocationDto location = playerDto.getLocation();
                                        location.setEntranceName(town.getEntranceName());
                                        location.setMapName(town.getMapName());
                                        location.setRowIndex(town.getRowIndex());
                                        location.setColumnIndex(town.getColumnIndex());
                                        location.setFacing(town.getTownCenterDirection());
                                        playerAssembler.updatePlayer(player, playerDto, true);
                                        saveManager.save(playerDto);
                                        playerRepository.save(player);
                                    } else {
                                        throw new IllegalStateException(String.format("Not enough MP to cast %s", spell.getSpellName()));
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(String.format("Unsupported spell %s", spell.getSpellName()));
                            }
                        }
                );
        return playerDto;
    }
}
