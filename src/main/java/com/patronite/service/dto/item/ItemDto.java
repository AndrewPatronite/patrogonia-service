package com.patronite.service.dto.item;

import com.patronite.service.model.ItemDetails;

import java.io.Serializable;

public class ItemDto implements Serializable {
    private static final long serialVersionUID = 2917860108349724536L;
    private int id = 0;
    private ItemDetails itemDetails;
    private boolean equipped;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }
}
