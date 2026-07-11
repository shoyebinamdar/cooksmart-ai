package com.cooksmart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single meal in the daily plan")
public class MealResponse {

    private String name;
    private Integer cookingTimeMinutes;
    private Double estimatedCost;
    private String description;

    public MealResponse() {
    }

    public MealResponse(String name, Integer cookingTimeMinutes, Double estimatedCost, String description) {
        this.name = name;
        this.cookingTimeMinutes = cookingTimeMinutes;
        this.estimatedCost = estimatedCost;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCookingTimeMinutes() {
        return cookingTimeMinutes;
    }

    public void setCookingTimeMinutes(Integer cookingTimeMinutes) {
        this.cookingTimeMinutes = cookingTimeMinutes;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
