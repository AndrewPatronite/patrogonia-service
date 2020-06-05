package com.patronite.service;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.battle.BattleManager;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.level.Level;
import com.patronite.service.level.LevelManager;
import com.patronite.service.model.Player;
import com.patronite.service.repository.PlayerRepository;
import com.patronite.service.save.SaveManager;
import com.patronite.service.spell.Spell;
import com.patronite.service.message.PlayerMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        return playerAssembler.dto(player, spells);
    }

    public PlayerDto update(PlayerDto updatedPlayerDto) {
        Player player = playerRepository.getOne(updatedPlayerDto.getId());
        if (updatedPlayerDto.isSaveGame()) {
            saveManager.save(updatedPlayerDto);
        } else {
            battleManager.findBattleInProgressAtLocation(updatedPlayerDto.getLocation())
                    .ifPresentOrElse(battle -> {
                                battleManager.joinBattle(updatedPlayerDto.getStats(), battle.getId());
                                updatedPlayerDto.setBattleId(battle.getId().toString());
                            },
                            () -> battleManager.spawnOrDontSpawnBattle(updatedPlayerDto, player.getLocation())
                                    .ifPresent(battle -> updatedPlayerDto.setBattleId(battle.getId().toString())));
        }
        playerAssembler.updatePlayer(player, updatedPlayerDto);
        playerRepository.save(player);
        updatedPlayerDto.setLastUpdate(new Date());
        playerMessenger.publishPlayerMessage(updatedPlayerDto);
        logger.info("Updated player {}", updatedPlayerDto);
        return updatedPlayerDto;
    }

    public List<PlayerDto> getPlayers(String mapName) {
        List<Player> players = playerRepository.findByLocation(mapName);
        return players.stream().map(player -> {
            List<Spell> spells = levelManager.getSpells(player.getStats().getLevel());
            PlayerDto playerDto = playerAssembler.dto(player, spells);
            battleManager.getPlayerBattleId(player.getId())
                    .ifPresent(battleId -> playerDto.setBattleId(battleId.toString()));
            return playerDto;
        }).collect(Collectors.toList());
    }

    public void loadLastSave(int playerId) {
        battleManager.removePlayerFromBattle(playerId);
        saveManager.loadLastSave(playerId);
    }
}
