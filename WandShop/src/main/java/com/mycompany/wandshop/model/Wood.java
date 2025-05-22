package com.mycompany.wandshop.model;

/**
 *
 * @author lihac
 */
public class Wood {
    private String type;
    private int quantityInStock;

    public Wood() {
    }

    public Wood(String type, int quantityInStock) {
        this.type = type;
        this.quantityInStock = quantityInStock;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    @Override
    public String toString() {
        return type;
    }
}
