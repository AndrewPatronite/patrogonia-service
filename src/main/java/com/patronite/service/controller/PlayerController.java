package com.patronite.service.controller;

import com.patronite.service.PlayerService;
import com.patronite.service.dto.player.PlayerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/player")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Integer> create(@RequestBody PlayerDto player) {
        ResponseEntity<Integer> response;
        try {
            int playerId = playerService.create(player);
            response = new ResponseEntity<>(playerId, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException ex) {
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Integer> login(@RequestParam String username, @RequestParam String password) {
        ResponseEntity<Integer> response;
        try {
            int playerId = playerService.login(username, password);
            response = new ResponseEntity<>(playerId, HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/getPlayers/{mapName}")
    public ResponseEntity<List<PlayerDto>> getPlayers(@PathVariable String mapName) {
        ResponseEntity<List<PlayerDto>> response;
        try {
            List<PlayerDto> players = playerService.getPlayers(mapName);
            response = new ResponseEntity<>(players, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/get/{playerId}")
    public ResponseEntity<PlayerDto> getPlayer(@PathVariable int playerId) {
        ResponseEntity<PlayerDto> response;
        try {
            PlayerDto player = playerService.getPlayer(playerId);
            response = new ResponseEntity<>(player, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PutMapping(value = "/update")
    public ResponseEntity<PlayerDto> update(@RequestBody PlayerDto player) {
        ResponseEntity<PlayerDto> response;
        try {
            response = new ResponseEntity<>(playerService.update(player), HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PutMapping(value = "/loadSave/{playerId}")
    public ResponseEntity<Integer> loadSave(@PathVariable int playerId) {
        ResponseEntity<Integer> response;
        try {
            playerService.loadLastSave(playerId);
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
