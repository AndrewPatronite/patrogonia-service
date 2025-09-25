package com.patronite.service.stats;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.dto.player.StatsDto;
import com.patronite.service.model.Stats;
import com.patronite.service.repository.StatsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StatsManager {
    private final StatsRepository statsRepository;

    public StatsManager(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void updateStats(int playerId, StatsDto statsDto) {
        Stats stats = statsRepository.getOne(playerId);
        PlayerAssembler.updateStats(stats, statsDto, false);
        statsRepository.save(stats);
    }
}
