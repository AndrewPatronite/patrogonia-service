package com.patronite.service.message;

import com.google.gson.Gson;
import com.patronite.service.dto.BattleDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class BattleMessenger extends AbstractMessenger {
    public static final int INITIAL_DELAY = 0;
    public static final int DELAY = 1500;

    public void publishBattleMessage(BattleDto battleDto) {
        String battleMessage = new Gson().toJson(battleDto);
        logger.debug("{}", battleDto.getLog());
        handleTextMessage(null, new TextMessage(battleMessage));
    }

    //TODO AP break battleMessage up into smaller message tyes as an alternative to this
    public void publishBattleMessagesForCurrentRound(BattleDto battle) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> {
                    try {
                        battle.getLog().stream()
                                .filter(logEntry -> !logEntry.isDelivered())
                                .findFirst()
                                .ifPresent(undeliveredEntry -> {
                                    undeliveredEntry.setDelivered(true);
                                    publishBattleMessage(battle);
                                });
                    } catch (Exception ex) {
                        logger.error("A problem occurred while sending messages: ", ex);
                    }
                }, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
    }
}
