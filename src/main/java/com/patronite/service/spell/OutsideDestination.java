package com.patronite.service.spell;

public enum OutsideDestination {
    ATORIS(7, 4),
    GRIMES(21, 7);

    private final int rowIndex;
    private final int columnIndex;

    OutsideDestination(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
}
