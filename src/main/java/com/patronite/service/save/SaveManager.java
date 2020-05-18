package com.patronite.service.save;

import com.patronite.service.assembler.PlayerAssembler;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.model.Player;
import com.patronite.service.model.Save;
import com.patronite.service.repository.PlayerRepository;
import com.patronite.service.repository.SaveRepository;
import org.springframework.stereotype.Component;

@Component
public class SaveManager {
    private final SaveRepository saveRepository;
    private final PlayerRepository playerRepository;

    public SaveManager(SaveRepository saveRepository, PlayerRepository playerRepository) {
        this.saveRepository = saveRepository;
        this.playerRepository = playerRepository;
    }

    public void save(PlayerDto updatedPlayerDto) {
        saveRepository.save(PlayerAssembler.save(updatedPlayerDto));
    }

    public void loadLastSave(int playerId) {
        Player player = playerRepository.getOne(playerId);
        Save save = saveRepository.getOne(playerId);
        PlayerAssembler.loadSave(player, save);
        playerRepository.save(player);
    }
}
