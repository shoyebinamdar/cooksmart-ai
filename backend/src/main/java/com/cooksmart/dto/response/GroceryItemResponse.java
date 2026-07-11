package com.cooksmart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Grocery item required for the meal plan")
public class GroceryItemResponse {

    private String name;
    private String quantity;

    public GroceryItemResponse() {
    }

    public GroceryItemResponse(String name, String quantity) {
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
