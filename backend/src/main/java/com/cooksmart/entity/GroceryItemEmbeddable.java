package com.cooksmart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GroceryItemEmbeddable {

    @Column(name = "grocery_name")
    private String name;

    @Column(name = "grocery_quantity")
    private String quantity;

    public GroceryItemEmbeddable() {
    }

    public GroceryItemEmbeddable(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
