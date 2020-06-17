package com.patronite.service;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.battle.BattleManager;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.level.Level;
import com.patronite.service.level.LevelManager;
import com.patronite.service.message.PlayerMessenger;
import com.patronite.service.model.Player;
import com.patronite.service.model.Stats;
import com.patronite.service.repository.PlayerRepository;
import com.patronite.service.save.SaveManager;
import com.patronite.service.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {
    @InjectMocks private PlayerService subject;
    @Mock private PlayerRepository playerRepository;
    @Mock private PlayerMessenger playerMessenger;
    @Mock private BattleManager battleManager;
    @Mock private LevelManager levelManager;
    @Mock private SaveManager saveManager;
    @Mock private PlayerAssembler playerAssembler;
    private static final int PLAYER_ID = 1;
    private static final int PLAYER_LEVEL = 3;
    @Mock private Player player;
    @Mock private Stats playerStats;

    @BeforeEach
    void setup() {
        lenient().when(player.getId()).thenReturn(PLAYER_ID);
        lenient().when(player.getStats()).thenReturn(playerStats);
        lenient().when(playerStats.getLevel()).thenReturn(PLAYER_LEVEL);
        lenient().when(playerRepository.getOne(PLAYER_ID)).thenReturn(player);
    }

    @Test
    void create() {
        PlayerDto playerDto = mock(PlayerDto.class);
        Level levelStats = mock(Level.class);
        when(levelManager.getLevel(1)).thenReturn(levelStats);
        when(playerAssembler.entity(playerDto, levelStats)).thenReturn(player);
        when(playerRepository.save(player)).thenReturn(player);
        when(playerAssembler.dto(eq(player), anyList())).thenReturn(playerDto);

        assertEquals(PLAYER_ID, subject.create(playerDto));

        verify(saveManager).save(playerDto);
    }

    @Test
    void createUsernameExists() {
        String username = "Conan";
        PlayerDto playerDto = mock(PlayerDto.class);
        when(playerDto.getUsername()).thenReturn(username);
        when(playerRepository.findByUsername(username)).thenReturn(mock(Player.class));

        assertThrows(IllegalArgumentException.class, () -> subject.create(playerDto));

        verify(levelManager, never()).getLevel(anyInt());
    }

    @Test
    void login() {
        String username = "Redwan";
        String password = "d3i@ke7-ncy-4";
        Integer playerId = 1234;
        Player player = mock(Player.class);
        when(player.getPassword()).thenReturn(password);
        when(player.getId()).thenReturn(playerId);
        when(playerRepository.findByLogin(username, password)).thenReturn(player);

        assertEquals(playerId, subject.login(username, password));
    }

    @Test
    void loginBadCredentials() {
        assertThrows(BadCredentialsException.class, () -> subject.login(null, null));
    }

    @Test
    void getPlayer() {
        List<Spell> spells = emptyList();
        PlayerDto playerDto = mock(PlayerDto.class);
        when(levelManager.getSpells(player.getStats().getLevel())).thenReturn(spells);
        when(playerAssembler.dto(player, spells)).thenReturn(playerDto);

        assertSame(playerDto, subject.getPlayer(PLAYER_ID));
    }

    @Test
    void updateSavingGame() {
        PlayerDto updatedPlayerDto = mock(PlayerDto.class);
        List<Spell> spells = singletonList(Spell.HEAL);
        when(updatedPlayerDto.getId()).thenReturn(PLAYER_ID);
        when(updatedPlayerDto.isSaveGame()).thenReturn(true);
        when(levelManager.getSpells(PLAYER_LEVEL)).thenReturn(spells);

        assertSame(updatedPlayerDto, subject.update(updatedPlayerDto));

        verify(saveManager).save(updatedPlayerDto);
        verify(playerAssembler).updatePlayer(player, updatedPlayerDto);
        verify(playerRepository).save(player);
        verify(playerAssembler).setSpellDtos(updatedPlayerDto, spells);
        verify(updatedPlayerDto).setLastUpdate(any(Date.class));
        verify(playerMessenger).publishPlayerMessage(updatedPlayerDto);
    }

    @Test
    void updateJoiningAnotherPlayersBattle() {
        PlayerDto updatedPlayerDto = mock(PlayerDto.class);
        LocationDto location = mock(LocationDto.class);
        BattleDto battle = mock(BattleDto.class);
        UUID battleId = UUID.randomUUID();
        List<Spell> spells = singletonList(Spell.HEAL);
        when(updatedPlayerDto.getId()).thenReturn(PLAYER_ID);
        when(battle.getId()).thenReturn(battleId);
        when(updatedPlayerDto.getLocation()).thenReturn(location);
        when(battleManager.findBattleInProgressAtLocation(location)).thenReturn(Optional.of(battle));
        when(levelManager.getSpells(PLAYER_LEVEL)).thenReturn(spells);

        assertSame(updatedPlayerDto, subject.update(updatedPlayerDto));

        verify(saveManager, never()).save(updatedPlayerDto);
        verify(battleManager, never()).spawnOrDontSpawnBattle(updatedPlayerDto, player.getLocation());
        verify(battleManager).joinBattle(updatedPlayerDto.getStats(), battle.getId());
        verify(updatedPlayerDto).setBattleId(battleId.toString());
        verify(playerAssembler).updatePlayer(player, updatedPlayerDto);
        verify(playerRepository).save(player);
        verify(playerAssembler).setSpellDtos(updatedPlayerDto, spells);
        verify(updatedPlayerDto).setLastUpdate(any(Date.class));
        verify(playerMessenger).publishPlayerMessage(updatedPlayerDto);
    }

    @Test
    void updateGeneratingBattle() {
        PlayerDto updatedPlayerDto = mock(PlayerDto.class);
        LocationDto location = mock(LocationDto.class);
        BattleDto battle = mock(BattleDto.class);
        UUID battleId = UUID.randomUUID();
        List<Spell> spells = singletonList(Spell.HEAL);
        when(updatedPlayerDto.getId()).thenReturn(PLAYER_ID);
        when(battle.getId()).thenReturn(battleId);
        when(updatedPlayerDto.getLocation()).thenReturn(location);
        when(battleManager.findBattleInProgressAtLocation(location)).thenReturn(Optional.empty());
        when(battleManager.spawnOrDontSpawnBattle(updatedPlayerDto, player.getLocation())).thenReturn(Optional.of(battle));
        when(levelManager.getSpells(PLAYER_LEVEL)).thenReturn(spells);

        assertSame(updatedPlayerDto, subject.update(updatedPlayerDto));

        verify(saveManager, never()).save(updatedPlayerDto);
        verify(updatedPlayerDto).setBattleId(battleId.toString());
        verify(playerAssembler).updatePlayer(player, updatedPlayerDto);
        verify(playerRepository).save(player);
        verify(playerAssembler).setSpellDtos(updatedPlayerDto, spells);
        verify(updatedPlayerDto).setLastUpdate(any(Date.class));
        verify(playerMessenger).publishPlayerMessage(updatedPlayerDto);
    }

    @Test
    void getPlayers() {
        String mapName = "field1";
        int playerInBattleId = 911;
        UUID battleId = UUID.randomUUID();
        Player playerInBattle = mock(Player.class);
        PlayerDto playerInBattleDto = mock(PlayerDto.class);
        PlayerDto playerDto = mock(PlayerDto.class);
        when(playerInBattle.getStats()).thenReturn(mock(Stats.class));
        when(playerInBattle.getId()).thenReturn(playerInBattleId);
        when(playerRepository.findByLocation(mapName)).thenReturn(asList(player, playerInBattle));
        when(playerAssembler.dto(eq(player), anyList())).thenReturn(playerDto);
        when(playerAssembler.dto(eq(playerInBattle), anyList())).thenReturn(playerInBattleDto);
        when(battleManager.getPlayerBattleId(PLAYER_ID)).thenReturn(Optional.empty());
        when(battleManager.getPlayerBattleId(playerInBattleId)).thenReturn(Optional.of(battleId));

        assertEquals(asList(playerDto, playerInBattleDto), subject.getPlayers(mapName));
        verify(playerDto, never()).setBattleId(anyString());
        verify(playerInBattleDto).setBattleId(battleId.toString());
    }

    @Test
    void loadLastSave() {
        subject.loadLastSave(PLAYER_ID);

        verify(battleManager).removePlayerFromBattle(PLAYER_ID);
        verify(saveManager).loadLastSave(PLAYER_ID);
    }
}