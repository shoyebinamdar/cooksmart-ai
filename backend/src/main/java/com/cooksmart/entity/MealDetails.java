package com.cooksmart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MealDetails {

    @Column(name = "meal_name")
    private String name;

    @Column(name = "cooking_time_minutes")
    private Integer cookingTimeMinutes;

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    @Column(name = "description", length = 1000)
    private String description;

    public MealDetails() {
    }

    public MealDetails(String name, Integer cookingTimeMinutes, Double estimatedCost, String description) {
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
