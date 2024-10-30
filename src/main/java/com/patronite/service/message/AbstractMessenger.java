package com.patronite.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public abstract class AbstractMessenger extends TextWebSocketHandler {
    List<WebSocketSession> subscriptions = newArrayList();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        subscriptions.forEach(subscription -> {
            try {
                synchronized (subscription) {
                    subscription.sendMessage(message);
                }
            } catch (IOException e) {
                logger.error("An error occurred while sending message ", e);
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        subscriptions.add(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        logger.error("A transport error occurred ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        subscriptions.remove(session);
        logger.debug("Connection for session {} closed with status {}", session, status);
    }
}
