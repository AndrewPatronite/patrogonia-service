package com.patronite.service.spell;

import java.util.Optional;

import static java.util.Arrays.asList;

public enum ReturnDestination {
    DEWHURST("Atoris", 6, 7),
    FERNSWORTH("Grimes", 8, 16),
    EASTHAVEN("Grimes", 12, 42);

    private final int rowIndex;
    private final int columnIndex;
    private final String mapName;

    ReturnDestination(String mapName, int rowIndex, int columnIndex) {
        this.mapName = mapName;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public static String getTownName(String mapName, int rowIndex, int columnIndex) {
        return asList(ReturnDestination.values()).stream()
                .filter(returnDestination -> returnDestination.getMapName().equals(mapName)
                        && returnDestination.getRowIndex() == rowIndex
                        && returnDestination.getColumnIndex() == columnIndex
                ).findFirst()
                .map(Enum::name)
                .orElse(null);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getMapName() {
        return mapName;
    }
}
