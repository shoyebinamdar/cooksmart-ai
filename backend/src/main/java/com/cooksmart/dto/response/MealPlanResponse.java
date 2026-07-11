package com.cooksmart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "Complete generated meal plan response")
public class MealPlanResponse {

    private UUID id;
    private Instant createdAt;
    private Integer peopleCount;
    private String diet;
    private String cuisine;
    private String cookingTime;
    private MealResponse breakfast;
    private MealResponse lunch;
    private MealResponse dinner;
    private List<GroceryItemResponse> groceryList = new ArrayList<>();
    private List<SubstituteResponse> substitutions = new ArrayList<>();
    private BudgetAnalysisResponse budgetAnalysis;
    private List<String> cookingTimeline = new ArrayList<>();
    private String plannerSource;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public MealResponse getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(MealResponse breakfast) {
        this.breakfast = breakfast;
    }

    public MealResponse getLunch() {
        return lunch;
    }

    public void setLunch(MealResponse lunch) {
        this.lunch = lunch;
    }

    public MealResponse getDinner() {
        return dinner;
    }

    public void setDinner(MealResponse dinner) {
        this.dinner = dinner;
    }

    public List<GroceryItemResponse> getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(List<GroceryItemResponse> groceryList) {
        this.groceryList = groceryList;
    }

    public List<SubstituteResponse> getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(List<SubstituteResponse> substitutions) {
        this.substitutions = substitutions;
    }

    public BudgetAnalysisResponse getBudgetAnalysis() {
        return budgetAnalysis;
    }

    public void setBudgetAnalysis(BudgetAnalysisResponse budgetAnalysis) {
        this.budgetAnalysis = budgetAnalysis;
    }

    public List<String> getCookingTimeline() {
        return cookingTimeline;
    }

    public void setCookingTimeline(List<String> cookingTimeline) {
        this.cookingTimeline = cookingTimeline;
    }

    public String getPlannerSource() {
        return plannerSource;
    }

    public void setPlannerSource(String plannerSource) {
        this.plannerSource = plannerSource;
    }
}
