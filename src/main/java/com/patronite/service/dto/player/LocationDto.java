package com.patronite.service.dto.player;

import com.google.common.base.Objects;

import java.io.Serializable;

public class LocationDto implements Serializable {
    private static final long serialVersionUID = 5185534605422750333L;
    private String mapName;
    private int rowIndex;
    private int columnIndex;
    private String facing = "down";

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDto that = (LocationDto) o;
        return rowIndex == that.rowIndex &&
                columnIndex == that.columnIndex &&
                Objects.equal(mapName, that.mapName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mapName, rowIndex, columnIndex);
    }

    @Override
    public String toString() {
        return "LocationDto{" +
                "mapName='" + mapName + '\'' +
                ", rowIndex=" + rowIndex +
                ", columnIndex=" + columnIndex +
                ", facing='" + facing + '\'' +
                '}';
    }
}
