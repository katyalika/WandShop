package com.mycompany.wandshop.model;

import java.time.LocalDate;

/**
 *
 * @author lihac
 */
public class Purchase {
    private int id;
    private int customerId;
    private LocalDate purchaseDate; 
    private double cost;
    private int wandId;

    public Purchase() {
    }

    public Purchase(int id, int customerId, LocalDate purchaseDate, double cost, int wandId) {
        this.id = id;
        this.customerId = customerId;
        this.purchaseDate = purchaseDate;
        this.cost = cost;
        this.wandId = wandId;
    }

    public Purchase(int customerId, LocalDate purchaseDate, double cost, int wandId) {
        this.customerId = customerId;
        this.purchaseDate = purchaseDate;
        this.cost = cost;
        this.wandId = wandId;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getWandId() {
        return wandId;
    }

    public void setWandId(int wandId) {
        this.wandId = wandId;
    }
}
