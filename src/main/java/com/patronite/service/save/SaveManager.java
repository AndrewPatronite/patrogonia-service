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

    public void create(Player player) {
        saveRepository.save(PlayerAssembler.save(new Save(), player));
    }

    public void save(Player player, PlayerDto updatedPlayerDto) {
        PlayerAssembler.updatePlayer(player, updatedPlayerDto, true);
        playerRepository.save(player);
        Save save = saveRepository.getOne(player.getId());
        saveRepository.save(PlayerAssembler.save(save, player));
    }

    public void loadLastSave(int playerId) {
        Player player = playerRepository.getOne(playerId);
        Save save = saveRepository.getOne(playerId);
        PlayerAssembler.loadSave(player, save);
        playerRepository.save(player);
    }
}
