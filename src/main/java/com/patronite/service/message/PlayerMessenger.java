package com.patronite.service.message;

import com.google.gson.Gson;
import com.patronite.service.dto.player.PlayerDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class PlayerMessenger extends AbstractMessenger {
    public void publishPlayerMessage(PlayerDto playerDto) {
        String playerMessage = new Gson().toJson(playerDto);
        handleTextMessage(null, new TextMessage(playerMessage));
    }
}
