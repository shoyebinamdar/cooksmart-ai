package com.cooksmart.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal model produced by AI or rule-based planners before persistence.
 */
public class GeneratedMealPlan {

    private MealDraft breakfast;
    private MealDraft lunch;
    private MealDraft dinner;
    private List<GroceryDraft> groceryList = new ArrayList<>();
    private List<SubstituteDraft> substitutions = new ArrayList<>();
    private List<String> cookingTimeline = new ArrayList<>();
    private Double estimatedCost;
    private String budgetSuggestion;
    private String source;

    public MealDraft getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(MealDraft breakfast) {
        this.breakfast = breakfast;
    }

    public MealDraft getLunch() {
        return lunch;
    }

    public void setLunch(MealDraft lunch) {
        this.lunch = lunch;
    }

    public MealDraft getDinner() {
        return dinner;
    }

    public void setDinner(MealDraft dinner) {
        this.dinner = dinner;
    }

    public List<GroceryDraft> getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(List<GroceryDraft> groceryList) {
        this.groceryList = groceryList;
    }

    public List<SubstituteDraft> getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(List<SubstituteDraft> substitutions) {
        this.substitutions = substitutions;
    }

    public List<String> getCookingTimeline() {
        return cookingTimeline;
    }

    public void setCookingTimeline(List<String> cookingTimeline) {
        this.cookingTimeline = cookingTimeline;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getBudgetSuggestion() {
        return budgetSuggestion;
    }

    public void setBudgetSuggestion(String budgetSuggestion) {
        this.budgetSuggestion = budgetSuggestion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public static class MealDraft {
        private String name;
        private Integer cookingTimeMinutes;
        private Double estimatedCost;
        private String description;
        private List<String> ingredients = new ArrayList<>();

        public MealDraft() {
        }

        public MealDraft(String name, Integer cookingTimeMinutes, Double estimatedCost, String description) {
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

        public List<String> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<String> ingredients) {
            this.ingredients = ingredients;
        }
    }

    public static class GroceryDraft {
        private String name;
        private String quantity;

        public GroceryDraft() {
        }

        public GroceryDraft(String name, String quantity) {
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

    public static class SubstituteDraft {
        private String ingredient;
        private String alternative;

        public SubstituteDraft() {
        }

        public SubstituteDraft(String ingredient, String alternative) {
            this.ingredient = ingredient;
            this.alternative = alternative;
        }

        public String getIngredient() {
            return ingredient;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        public String getAlternative() {
            return alternative;
        }

        public void setAlternative(String alternative) {
            this.alternative = alternative;
        }
    }
}
