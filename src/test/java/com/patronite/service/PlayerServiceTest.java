package com.patronite.service;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.battle.BattleManager;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.player.LocationDto;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.dto.player.SpellDto;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.level.Level;
import com.patronite.service.level.LevelManager;
import com.patronite.service.location.Town;
import com.patronite.service.message.PlayerMessenger;
import com.patronite.service.model.Player;
import com.patronite.service.model.Stats;
import com.patronite.service.repository.PlayerRepository;
import com.patronite.service.save.SaveManager;
import com.patronite.service.spell.OutsideDestination;
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
    private static final int PLAYER_XP = 45;
    private static final int XP_TILL_NEXT_LEVEL = 10;
    @Mock private Player player;
    @Mock private Stats playerStats;

    @BeforeEach
    void setup() {
        lenient().when(player.getId()).thenReturn(PLAYER_ID);
        lenient().when(player.getStats()).thenReturn(playerStats);
        lenient().when(playerStats.getLevel()).thenReturn(PLAYER_LEVEL);
        lenient().when(playerStats.getXp()).thenReturn(PLAYER_XP);
        lenient().when(playerRepository.getOne(PLAYER_ID)).thenReturn(player);
        lenient().when(levelManager.getXpTillNextLevel(PLAYER_LEVEL, PLAYER_XP)).thenReturn(XP_TILL_NEXT_LEVEL);
    }

    @Test
    void create() {
        PlayerDto playerDto = mock(PlayerDto.class);
        Level levelStats = mock(Level.class);
        when(levelManager.getLevel(1)).thenReturn(levelStats);
        when(playerAssembler.entity(playerDto, levelStats)).thenReturn(player);
        when(playerRepository.save(player)).thenReturn(player);
        when(playerAssembler.dto(eq(player), anyList(), eq(XP_TILL_NEXT_LEVEL))).thenReturn(playerDto);

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
        when(playerAssembler.dto(player, spells, XP_TILL_NEXT_LEVEL)).thenReturn(playerDto);

        assertSame(playerDto, subject.getPlayer(PLAYER_ID));
    }

    @Test
    void updateSavingGame() {
        PlayerDto updatedPlayerDto = mock(PlayerDto.class);
        List<Spell> spells = singletonList(Spell.HEAL);
        boolean saveGame = true;
        when(updatedPlayerDto.getId()).thenReturn(PLAYER_ID);
        when(levelManager.getSpells(PLAYER_LEVEL)).thenReturn(spells);
        StatsDto playerStatsDto = mock(StatsDto.class);
        when(playerStatsDto.getXp()).thenReturn(PLAYER_XP);
        when(updatedPlayerDto.getStats()).thenReturn(playerStatsDto);
        when(levelManager.getXpTillNextLevel(PLAYER_LEVEL, PLAYER_XP)).thenReturn(15);

        assertSame(updatedPlayerDto, subject.update(updatedPlayerDto, saveGame));

        verify(saveManager).save(updatedPlayerDto);
        verify(playerAssembler).updatePlayer(player, updatedPlayerDto, saveGame);
        verify(playerRepository).save(player);
        verify(playerAssembler).setSpellDtos(updatedPlayerDto, spells);
        verify(playerStatsDto).setXpTillNextLevel(15);
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
        boolean saveGame = false;
        when(updatedPlayerDto.getId()).thenReturn(PLAYER_ID);
        when(battle.getId()).thenReturn(battleId);
        when(updatedPlayerDto.getLocation()).thenReturn(location);
        when(battleManager.findBattleInProgressAtLocation(location)).thenReturn(Optional.of(battle));
        when(levelManager.getSpells(PLAYER_LEVEL)).thenReturn(spells);
        StatsDto playerStatsDto = mock(StatsDto.class);
        when(playerStatsDto.getXp()).thenReturn(PLAYER_XP);
        when(updatedPlayerDto.getStats()).thenReturn(playerStatsDto);
        when(levelManager.getXpTillNextLevel(PLAYER_LEVEL, PLAYER_XP)).thenReturn(15);

        assertSame(updatedPlayerDto, subject.update(updatedPlayerDto, saveGame));

        verify(saveManager, never()).save(updatedPlayerDto);
        verify(battleManager, never()).spawnOrDontSpawnBattle(updatedPlayerDto, player.getLocation());
        verify(battleManager).joinBattle(updatedPlayerDto.getStats(), battle.getId());
        verify(updatedPlayerDto).setBattleId(battleId.toString());
        verify(playerAssembler).updatePlayer(player, updatedPlayerDto, saveGame);
        verify(playerRepository).save(player);
        verify(playerAssembler).setSpellDtos(updatedPlayerDto, spells);
        verify(playerStatsDto).setXpTillNextLevel(15);
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
        boolean saveGame = false;
        when(updatedPlayerDto.getId()).thenReturn(PLAYER_ID);
        when(battle.getId()).thenReturn(battleId);
        when(updatedPlayerDto.getLocation()).thenReturn(location);
        when(battleManager.findBattleInProgressAtLocation(location)).thenReturn(Optional.empty());
        when(battleManager.spawnOrDontSpawnBattle(updatedPlayerDto, player.getLocation())).thenReturn(Optional.of(battle));
        when(levelManager.getSpells(PLAYER_LEVEL)).thenReturn(spells);
        StatsDto playerStatsDto = mock(StatsDto.class);
        when(playerStatsDto.getXp()).thenReturn(PLAYER_XP);
        when(updatedPlayerDto.getStats()).thenReturn(playerStatsDto);
        when(levelManager.getXpTillNextLevel(PLAYER_LEVEL, PLAYER_XP)).thenReturn(15);

        assertSame(updatedPlayerDto, subject.update(updatedPlayerDto, saveGame));

        verify(saveManager, never()).save(updatedPlayerDto);
        verify(updatedPlayerDto).setBattleId(battleId.toString());
        verify(playerAssembler).updatePlayer(player, updatedPlayerDto, saveGame);
        verify(playerRepository).save(player);
        verify(playerAssembler).setSpellDtos(updatedPlayerDto, spells);
        verify(playerStatsDto).setXpTillNextLevel(15);
        verify(updatedPlayerDto).setLastUpdate(any(Date.class));
        verify(playerMessenger).publishPlayerMessage(updatedPlayerDto);
    }

    @Test
    void getPlayers() {
        String mapName = "Atoris";
        int playerInBattleId = 911;
        UUID battleId = UUID.randomUUID();
        Player playerInBattle = mock(Player.class);
        PlayerDto playerInBattleDto = mock(PlayerDto.class);
        PlayerDto playerDto = mock(PlayerDto.class);
        when(playerInBattle.getStats()).thenReturn(mock(Stats.class));
        when(playerInBattle.getId()).thenReturn(playerInBattleId);
        when(playerRepository.findByLocation(mapName)).thenReturn(asList(player, playerInBattle));
        when(playerAssembler.dto(eq(player), anyList(), eq(XP_TILL_NEXT_LEVEL))).thenReturn(playerDto);
        when(playerAssembler.dto(eq(playerInBattle), anyList(), anyInt())).thenReturn(playerInBattleDto);
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

    private PlayerDto getSpellCaster() {
        PlayerDto playerDto = mock(PlayerDto.class);
        StatsDto playerStatsDto = mock(StatsDto.class);
        LocationDto playerLocation = mock(LocationDto.class);
        when(playerDto.getSpells()).thenReturn(asList(new SpellDto("HEAL", 5, false, true),
                new SpellDto("RETURN", 5, false, false),
                new SpellDto("OUTSIDE", 5, false, false),
                new SpellDto("ICE", 5, true, true)
        ));
        when(playerDto.getStats()).thenReturn(playerStatsDto);
        lenient().when(playerDto.getId()).thenReturn(PLAYER_ID);
        when(playerStatsDto.getMp()).thenReturn(7);
        lenient().when(playerStatsDto.getHp()).thenReturn(10);
        lenient().when(playerStatsDto.getHpTotal()).thenReturn(50);
        lenient().when(playerDto.getLocation()).thenReturn(playerLocation);
        return playerDto;
    }

    @Test
    void castHeal() {
        PlayerDto playerDto = getSpellCaster();

        assertSame(playerDto, subject.castSpell(playerDto, "HEAL", Integer.toString(PLAYER_ID)));

        verify(playerDto.getStats()).setHp(Spell.HEAL.getEffect() +  playerDto.getStats().getHp());
        verify(playerDto.getStats()).setMp(playerDto.getStats().getMp() - Spell.HEAL.getMp());
    }

    @Test
    void castHealToMaxHp() {
        PlayerDto playerDto = getSpellCaster();
        int hpTotal = playerDto.getStats().getHpTotal();
        int playerHp = hpTotal - 1;
        when(playerDto.getStats().getHp()).thenReturn(playerHp);

        assertSame(playerDto, subject.castSpell(playerDto, "HEAL", Integer.toString(PLAYER_ID)));

        verify(playerDto.getStats()).setHp(hpTotal);
        verify(playerDto.getStats()).setMp(playerDto.getStats().getMp() - Spell.HEAL.getMp());
    }

    @Test
    void castHealNotEnoughMp() {
        PlayerDto playerDto = getSpellCaster();
        when(playerDto.getStats().getMp()).thenReturn(2);

        assertThrows(IllegalStateException.class, () -> subject.castSpell(playerDto, "HEAL", Integer.toString(PLAYER_ID)));

        verify(playerDto.getStats(), never()).getHpTotal();
    }

    @Test
    void castOutside() {
        PlayerDto playerDto = getSpellCaster();
        boolean saveGame = false;
        when(playerDto.getLocation().getMapName()).thenReturn("Lava Grotto");
        when(playerRepository.getOne(PLAYER_ID)).thenReturn(player);

        assertSame(playerDto, subject.castSpell(playerDto, "OUTSIDE", "Atoris"));

        verify(playerDto.getLocation()).setEntranceName("Lava Grotto");
        verify(playerDto.getLocation()).setMapName("Atoris");
        verify(playerDto.getLocation()).setRowIndex(OutsideDestination.ATORIS.getRowIndex());
        verify(playerDto.getLocation()).setColumnIndex(OutsideDestination.ATORIS.getColumnIndex());
        verify(playerAssembler).updatePlayer(player, playerDto, saveGame);
        verify(playerRepository).save(player);
        verify(playerDto.getStats()).setMp(playerDto.getStats().getMp() - Spell.OUTSIDE.getMp());
    }

    @Test
    void castOutsideNotEnoughMp() {
        PlayerDto playerDto = getSpellCaster();
        when(playerDto.getStats().getMp()).thenReturn(2);

        assertThrows(IllegalStateException.class, () -> subject.castSpell(playerDto, "OUTSIDE", "Atoris"));
        verify(playerRepository, never()).getOne(anyInt());
    }

    @Test
    void castReturn() {
        PlayerDto playerDto = getSpellCaster();
        boolean saveGame = true;
        when(playerRepository.getOne(PLAYER_ID)).thenReturn(player);

        assertSame(playerDto, subject.castSpell(playerDto, "RETURN", "Dewhurst"));

        verify(playerDto.getLocation()).setEntranceName(Town.DEWHURST.getEntranceName());
        verify(playerDto.getLocation()).setMapName(Town.DEWHURST.getMapName());
        verify(playerDto.getLocation()).setRowIndex(Town.DEWHURST.getRowIndex());
        verify(playerDto.getLocation()).setColumnIndex(Town.DEWHURST.getColumnIndex());
        verify(playerAssembler).updatePlayer(player, playerDto, saveGame);
        verify(playerRepository).save(player);
        verify(playerDto.getStats()).setMp(playerDto.getStats().getMp() - Spell.RETURN.getMp());
    }

    @Test
    void castReturnNotEnoughMp() {
        PlayerDto playerDto = getSpellCaster();
        when(playerDto.getStats().getMp()).thenReturn(2);

        assertThrows(IllegalStateException.class, () -> subject.castSpell(playerDto, "RETURN", "Dewhurst"));
        verify(playerRepository, never()).getOne(anyInt());
    }

    @Test
    void castUnsupportedSpell() {
        PlayerDto playerDto = getSpellCaster();
        assertThrows(IllegalArgumentException.class, () -> subject.castSpell(playerDto, "ICE", null));
    }
}