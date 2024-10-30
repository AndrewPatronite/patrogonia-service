package com.patronite.service.controller;

import com.patronite.service.NpcService;
import com.patronite.service.dto.npc.NpcDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/npc")
public class NpcController {
    private final NpcService npcService;
    private final Logger logger = Logger.getLogger("NpcController");

    public NpcController(NpcService npcService) {
        this.npcService = npcService;
    }

    @GetMapping("/getNpcs/{mapName}")
    public ResponseEntity<List<NpcDto>> getNpcs(@PathVariable String mapName) {
        ResponseEntity<List<NpcDto>> response;
        try {
            List<NpcDto> npcs = npcService.getNpcs(mapName);
            response = new ResponseEntity<>(npcs, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }

    @PutMapping(value = "/update")
    public ResponseEntity<NpcDto> update(@RequestBody NpcDto npcDto) {
        ResponseEntity<NpcDto> response;
        try {
            response = new ResponseEntity<>(npcService.update(npcDto), HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }
}
