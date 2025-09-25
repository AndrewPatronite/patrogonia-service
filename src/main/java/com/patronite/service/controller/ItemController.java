package com.patronite.service.controller;

import com.patronite.service.ItemService;
import com.patronite.service.dto.player.PlayerDto;
import com.patronite.service.model.ItemDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/item")
public class ItemController {
    private final ItemService itemService;
    private final Logger logger = Logger.getLogger("ItemController");

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/getShopInventory/{mapName}/{shopType}")
    public ResponseEntity<List<ItemDetails>> getShopInventory(@PathVariable String mapName, @PathVariable String shopType) {
        ResponseEntity<List<ItemDetails>> response;
        try {
            response = new ResponseEntity<>(itemService.getShopInventory(mapName, shopType), HttpStatus.OK);
        }
        catch (IllegalArgumentException ex) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            logger.warning(ex.getMessage());
        }
        catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }

    @PutMapping(value = "/purchaseItem/{itemName}")
    public ResponseEntity<PlayerDto> purchaseItem(@RequestBody PlayerDto player, @PathVariable String itemName) {
        ResponseEntity<PlayerDto> response;
        try {
            response = new ResponseEntity<>(itemService.purchaseItem(player, itemName), HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }

    @PutMapping(value = "sellItem/{itemId}")
    public ResponseEntity<PlayerDto> sellITem(@RequestBody PlayerDto player, @PathVariable Integer itemId) {
        ResponseEntity<PlayerDto> response;
        try {
            response = new ResponseEntity<>(itemService.sellItem(player, itemId), HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }

    @PutMapping(value = {"useItem/{itemId}","useItem/{itemId}/{targetId}"})
    public ResponseEntity<PlayerDto> useItem(@RequestBody PlayerDto player, @PathVariable Integer itemId, @PathVariable Optional<String> targetId) {
        ResponseEntity<PlayerDto> response;
        try {
            PlayerDto playerDto = itemService.useItem(player, itemId, targetId);
            response = new ResponseEntity<>(playerDto, HttpStatus.OK);
        }
        catch (IllegalArgumentException ex) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            logger.warning(ex.getMessage());
        }
        catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.warning(Arrays.toString(ex.getStackTrace()));
        }
        return response;
    }
}
