package com.patronite.service.dto.npc;

import java.io.Serializable;
import java.util.Date;

public class NpcDto implements Serializable {
    private static final long serialVersionUID = -391584762798278149L;
    private String name;
    private String currentMapName;
    private int currentRowIndex;
    private int currentColumnIndex;
    private int movementRange;
    private int startingRowIndex;
    private int startingColumnIndex;
    private String type;
    private String directionFacing;
    private boolean isTalking;
    private final Date lastUpdate = new Date();

    public NpcDto() {
    }

    public NpcDto(String name, String currentMapName, int currentRowIndex, int currentColumnIndex, int movementRange, int startingRowIndex, int startingColumnIndex, String type, String directionFacing, boolean isTalking) {
        this.name = name;
        this.currentMapName = currentMapName;
        this.currentRowIndex = currentRowIndex;
        this.currentColumnIndex = currentColumnIndex;
        this.movementRange = movementRange;
        this.startingRowIndex = startingRowIndex;
        this.startingColumnIndex = startingColumnIndex;
        this.type = type;
        this.directionFacing = directionFacing;
        this.isTalking = isTalking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentMapName() {
        return currentMapName;
    }

    public void setCurrentMapName(String currentMapName) {
        this.currentMapName = currentMapName;
    }

    public int getCurrentRowIndex() {
        return currentRowIndex;
    }

    public void setCurrentRowIndex(int currentRowIndex) {
        this.currentRowIndex = currentRowIndex;
    }

    public int getCurrentColumnIndex() {
        return currentColumnIndex;
    }

    public void setCurrentColumnIndex(int currentColumnIndex) {
        this.currentColumnIndex = currentColumnIndex;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public void setMovementRange(int movementRange) {
        this.movementRange = movementRange;
    }

    public int getStartingRowIndex() {
        return startingRowIndex;
    }

    public void setStartingRowIndex(int startingRowIndex) {
        this.startingRowIndex = startingRowIndex;
    }

    public int getStartingColumnIndex() {
        return startingColumnIndex;
    }

    public void setStartingColumnIndex(int startingColumnIndex) {
        this.startingColumnIndex = startingColumnIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirectionFacing() {
        return directionFacing;
    }

    public void setDirectionFacing(String directionFacing) {
        this.directionFacing = directionFacing;
    }

    public boolean getIsTalking() {
        return isTalking;
    }

    public void setIsTalking(boolean talking) {
        isTalking = talking;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
}
