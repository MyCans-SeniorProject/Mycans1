package com.app.mycan1.model;

public class PantryItem {

    private int id;
    private String name;
    private String code;
    private int quantity;
    private int threshold;

    public PantryItem(){}

    public PantryItem(int id, String name, String code, int quantity, int threshold ) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.threshold = threshold;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "PantryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", quantity=" + quantity +
                ", threshold=" + threshold +
                '}';
    }
}
