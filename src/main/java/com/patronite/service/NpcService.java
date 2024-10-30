package com.patronite.service;

import com.patronite.service.dto.npc.NpcDto;
import com.patronite.service.message.NpcMessenger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NpcService {
    private static final int MOVEMENT_INTERVAL = 3000;
    private final NpcMessenger npcMessenger;
    private final Map<String, NpcDto> npcs = new ConcurrentHashMap<>() {
        {
            this.put("Alastair", new NpcDto("Alastair", "Dewhurst", 17, 6, 10, 17, 6, "Knight", "right", false));
            this.put("Barnaby", new NpcDto("Barnaby", "Dewhurst", 21, 12, 10, 21, 12, "Knight", "up", false));
            this.put("Finlay", new NpcDto("Finlay", "Fernsworth", 9, 11, 10, 9, 11, "Knight", "down", false));
            this.put("Nigel", new NpcDto("Nigel", "Easthaven", 7, 17, 10, 7, 17, "Knight", "down", false));
            this.put("Tristan", new NpcDto("Tristan", "Easthaven", 19, 7, 10, 19, 7, "Knight", "down", false));
        }
    };

    public NpcService(NpcMessenger npcMessenger) {
        this.npcMessenger = npcMessenger;
    }

    public NpcDto update(NpcDto npcDto) {
        Date now = new Date();
        NpcDto mostRecentNpcVersion = npcs.get(npcDto.getName());
        if (now.getTime() - mostRecentNpcVersion.getLastUpdate().getTime() > MOVEMENT_INTERVAL) {
            npcs.put(npcDto.getName(), npcDto);
            npcMessenger.publishNpcMessage(npcDto);
            return npcDto;
        } else {
            return mostRecentNpcVersion;
        }
    }

    public List<NpcDto> getNpcs(String mapName) {
        return npcs.values().stream().filter(npc -> npc.getCurrentMapName().equals(mapName)).collect(Collectors.toList());
    }
}
