package com.patronite.service.controller;

import com.patronite.service.BattleService;
import com.patronite.service.dto.BattleDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/battle")
public class BattleController {
    private final BattleService battleService;
    private final Logger logger = Logger.getLogger("BattleController");

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping(value = "/get/{battleId}")
    public ResponseEntity<BattleDto> getBattle(@PathVariable String battleId) {
        ResponseEntity<BattleDto> response;
        try {
            response = new ResponseEntity<>(battleService.getBattle(battleId), HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }

    @PostMapping(value = "/turn/{battleId}")
    public ResponseEntity<Integer> takeTurn(@PathVariable String battleId, @RequestParam int playerId, String playerAction, String targetId) {
        ResponseEntity<Integer> response;
        try {
            battleService.takeTurn(battleId, playerId, playerAction, targetId);
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }
}
