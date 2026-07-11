package com.cooksmart.dto.request;

import com.cooksmart.enums.AllergyType;
import com.cooksmart.enums.CookingTime;
import com.cooksmart.enums.CuisineType;
import com.cooksmart.enums.DietType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request payload to generate a personalized daily meal plan")
public class MealPlanRequest {

    @NotNull(message = "Number of people is required")
    @Min(value = 1, message = "Number of people must be greater than 0")
    @Schema(example = "2", minimum = "1")
    private Integer peopleCount;

    @NotNull(message = "Diet preference is required")
    @Schema(example = "Vegetarian")
    private DietType diet;

    @Schema(example = "Indian", description = "Optional; defaults to Any")
    private CuisineType cuisine = CuisineType.ANY;

    @NotNull(message = "Cooking time must be selected")
    @Schema(example = "15–30 min")
    private CookingTime cookingTime;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.01", message = "Budget must be positive")
    @Schema(example = "700", minimum = "0.01")
    private Double budget;

    @Size(max = 50, message = "You can list at most 50 available ingredients")
    @Schema(example = "[\"Rice\", \"Eggs\", \"Tomatoes\", \"Onions\", \"Milk\"]")
    private List<@Size(min = 1, max = 80, message = "Ingredient names cannot be empty") String> availableIngredients =
            new ArrayList<>();

    @Size(max = 10, message = "You can select at most 10 allergies")
    private List<AllergyType> allergies = new ArrayList<>();

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public DietType getDiet() {
        return diet;
    }

    public void setDiet(DietType diet) {
        this.diet = diet;
    }

    public CuisineType getCuisine() {
        return cuisine;
    }

    public void setCuisine(CuisineType cuisine) {
        this.cuisine = cuisine == null ? CuisineType.ANY : cuisine;
    }

    public CookingTime getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(CookingTime cookingTime) {
        this.cookingTime = cookingTime;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public List<String> getAvailableIngredients() {
        return availableIngredients;
    }

    public void setAvailableIngredients(List<String> availableIngredients) {
        this.availableIngredients = availableIngredients == null ? new ArrayList<>() : availableIngredients;
    }

    public List<AllergyType> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyType> allergies) {
        this.allergies = allergies == null ? new ArrayList<>() : allergies;
    }
}
