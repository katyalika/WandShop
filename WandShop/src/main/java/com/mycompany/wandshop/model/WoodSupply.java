package com.mycompany.wandshop.model;

/**
 *
 * @author lihac
 */
public class WoodSupply {
    private int supplyId;
    private String woodType;
    private int quantity;

    public WoodSupply() {
    }

    public WoodSupply(int supplyId, String woodType, int quantity) {
        this.supplyId = supplyId;
        this.woodType = woodType;
        this.quantity = quantity;
    }

    public int getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(int supplyId) {
        this.supplyId = supplyId;
    }

    public String getWoodType() {
        return woodType;
    }

    public void setWoodType(String woodType) {
        this.woodType = woodType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
