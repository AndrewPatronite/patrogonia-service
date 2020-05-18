package com.patronite.service.battle.turn;

import java.io.Serializable;

public class PlayerAction implements Serializable {
    private static final long serialVersionUID = -9179234265783923521L;
    private final String action;
    private final String targetId;

    public PlayerAction(String action, String targetId) {
        this.action = action;
        this.targetId = targetId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetId() {
        return targetId;
    }
}
