package com.patronite.service.controller;

import com.patronite.service.BattleService;
import com.patronite.service.dto.BattleDto;
import com.patronite.service.dto.player.StatsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/battle")
public class BattleController {
    private final BattleService battleService;

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
        }
        return response;
    }

    @PostMapping(value = "/join/{battleId}")
    public ResponseEntity<Integer> joinBattle(@PathVariable String battleId, @RequestBody StatsDto player) {
        ResponseEntity<Integer> response;
        try {
            battleService.joinBattle(battleId, player);
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
