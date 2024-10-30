package com.patronite.service.message;

import com.google.gson.Gson;
import com.patronite.service.dto.npc.NpcDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class NpcMessenger extends AbstractMessenger {
    public void publishNpcMessage(NpcDto npcDto) {
        String npcMessage = new Gson().toJson(npcDto);
        handleTextMessage(null, new TextMessage(npcMessage));
    }
}
