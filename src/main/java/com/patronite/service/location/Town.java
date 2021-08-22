package com.patronite.service.location;

public enum Town {
    DEWHURST("Dewhurst", "Atoris", 8, 24, "left"),
    FERNSWORTH("Fernsworth", "Grimes", 13, 0, "right"),
    EASTHAVEN("Easthaven", "Grimes", 13, 0, "right");

    private final int rowIndex;
    private final int columnIndex;
    private final String mapName;
    private final String entranceName;
    private final String townCenterDirection;

    Town(String mapName, String entranceName, int rowIndex, int columnIndex, String townCenterDirection) {
        this.mapName = mapName;
        this.entranceName = entranceName;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.townCenterDirection = townCenterDirection;
    }

    public String getMapName() {
        return mapName;
    }

    public String getEntranceName() {
        return entranceName;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getTownCenterDirection() {
        return townCenterDirection;
    }
}
