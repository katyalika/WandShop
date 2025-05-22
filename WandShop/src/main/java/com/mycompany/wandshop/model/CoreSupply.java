package com.mycompany.wandshop.model;

/**
 *
 * @author lihac
 */
public class CoreSupply {
    private int supplyId;
    private String coreType;
    private int quantity;

    public CoreSupply() {
    }

    public CoreSupply(int supplyId, String coreType, int quantity) {
        this.supplyId = supplyId;
        this.coreType = coreType;
        this.quantity = quantity;
    }

    public int getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(int supplyId) {
        this.supplyId = supplyId;
    }

    public String getCoreType() {
        return coreType;
    }

    public void setCoreType(String coreType) {
        this.coreType = coreType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
