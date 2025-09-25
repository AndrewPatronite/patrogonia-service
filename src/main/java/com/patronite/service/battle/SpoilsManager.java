package com.patronite.service.battle;

import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.item.ItemDto;
import com.patronite.service.dto.item.RewardDto;
import com.patronite.service.dto.player.StatsDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class SpoilsManager {
    public void distributeSpoils(BattleDto battle) {
        List<StatsDto> livingPlayers = battle.getLivingPlayers();
        Map<Integer, List<RewardDto>> playerRewards = battle.getPlayerRewards();
        Map<Integer, StatsDto> playerStats = battle.getPlayerStats();
        playerRewards.forEach((playerId, rewards) -> {
            StatsDto player = playerStats.get(playerId);
            if (player.isDead()) {
                distributeRewardsToPlayers(battle, rewards, player.getPlayerName(), livingPlayers);
            } else {
                distributeRewardsToPlayer(battle, player, rewards);
            }
        });
    }

    private void distributeRewardsToPlayer(BattleDto battle, StatsDto player, Collection<RewardDto> rewards) {
        AtomicInteger experience = new AtomicInteger();
        AtomicInteger gold = new AtomicInteger();
        List<ItemDto> items = newArrayList();
        rewards.forEach(reward -> {
            experience.addAndGet(reward.getExperience());
            gold.addAndGet(reward.getGold());
            items.addAll(reward.getItems());
        });
        player.setXp(player.getXp() + experience.get());
        player.setGold(player.getGold() + gold.get());
        if (items.isEmpty()) {
            battle.addLogEntry(String.format("%s receives %s experience and %s gold.",
                    player.getPlayerName(), experience.get(), gold.get()), player.getPlayerId());
        } else {
            //TODO AP add items
//            player.addItems(items);
            battle.addLogEntry(String.format("%s receives %s experience, %s gold, and %s.",
                    player.getPlayerName(), experience.get(), gold.get(),
                    items.stream().map(item -> item.getItemDetails().getName())
                            .collect(Collectors.joining(","))));
        }
    }

    private void distributeRewardsToPlayers(BattleDto battle, Collection<RewardDto> rewards, String playerName, List<StatsDto> players) {
        AtomicInteger gold = new AtomicInteger();
        List<ItemDto> items = newArrayList();
        rewards.forEach(reward -> {
            gold.addAndGet(reward.getGold());
            items.addAll(reward.getItems());
        });
        int goldForEach = gold.get() / players.size();
        int goldRemainder = gold.get() % players.size();
        players.get(0).setGold(players.get(0).getGold() + goldRemainder);
        players.forEach(player -> player.setGold(player.getGold() + goldForEach));
        for (int i = 0; i < items.size(); i++) {
            StatsDto player = players.get(i / players.size());
            ItemDto item = items.get(i);
            //TODO AP add items
//            player.addItem(item);
            battle.addLogEntry(String.format("%s receives %s since %s is dead.",
                    player.getPlayerName(), item.getItemDetails().getName(), playerName));
        }
        battle.addLogEntry(String.format("%s's %s gold was distributed to the party.", playerName, gold));
    }
}
