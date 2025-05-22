package com.mycompany.wandshop.model;
/**
 *
 * @author lihac
 */
import java.time.LocalDate;
import java.util.Objects;

public class Supply {

    private int id;
    private LocalDate date;
    private String supplier;

    public Supply() {
    }

    public Supply(int id, LocalDate date, String supplier) {
        this.id = id;
        this.date = date;
        this.supplier = supplier;
    }

    public Supply(LocalDate date, String supplier) {
        this.date = date;
        this.supplier = supplier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "Supply{"
                + "id=" + id
                + ", date=" + (date != null ? date.toString() : "null")
                + ", supplier='" + supplier + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Supply supply = (Supply) o;
        return id == supply.id
                && Objects.equals(date, supply.date)
                && Objects.equals(supplier, supply.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, supplier);
    }
}
