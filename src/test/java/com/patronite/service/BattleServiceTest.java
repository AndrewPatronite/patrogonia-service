package com.patronite.service;

import com.patronite.service.battle.BattleManager;
import com.patronite.service.dto.BattleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BattleServiceTest {
    @InjectMocks private BattleService subject;
    @Mock private BattleManager battleManager;
    private static final String BATTLE_ID = "abcdeffff123";

    @Test
    public void takeTurn() {
        int playerId = 1;
        String playerAction = "attack";
        String targetId = "ffffff";

        subject.takeTurn(BATTLE_ID, playerId, playerAction, targetId);

        verify(battleManager).takeTurn(BATTLE_ID, playerId, playerAction, targetId);
    }

    @Test
    public void getBattle() {
        BattleDto battle = mock(BattleDto.class);
        when(battleManager.getBattle(BATTLE_ID)).thenReturn(battle);

        assertSame(battle, subject.getBattle(BATTLE_ID));
    }
}
