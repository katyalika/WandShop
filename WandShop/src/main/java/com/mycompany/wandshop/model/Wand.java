package com.mycompany.wandshop.model;

/**
 *
 * @author lihac
 */
public class Wand {
    private int id;
    private double price;
    private String status;
    private String woodType;
    private String coreType;

    public Wand() {
    }

    public Wand(int id, double price, String status, String woodType, String coreType) {
        this.id = id;
        this.price = price;
        this.status = status;
        this.woodType = woodType;
        this.coreType = coreType;
    }

    public Wand(double price, String status, String woodType, String coreType) {
        this.price = price;
        this.status = status;
        this.woodType = woodType;
        this.coreType = coreType;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWoodType() {
        return woodType;
    }

    public void setWoodType(String woodType) {
        this.woodType = woodType;
    }

    public String getCoreType() {
        return coreType;
    }

    public void setCoreType(String coreType) {
        this.coreType = coreType;
    }

    @Override
    public String toString() {
        return "Wand{id=" + id + ", price=" + price + ", status=" + status
                + ", woodType=" + woodType + ", coreType=" + coreType + '}';
    } 
}
