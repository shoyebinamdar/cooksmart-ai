package com.cooksmart.entity;

import com.cooksmart.enums.BudgetStatus;
import com.cooksmart.enums.CookingTime;
import com.cooksmart.enums.CuisineType;
import com.cooksmart.enums.DietType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meal_plans")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Integer peopleCount;

    @Column(nullable = false)
    private Double budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietType diet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CuisineType cuisine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CookingTime cookingTime;

    @ElementCollection
    @CollectionTable(name = "meal_plan_available_ingredients", joinColumns = @JoinColumn(name = "meal_plan_id"))
    @Column(name = "ingredient")
    private List<String> availableIngredients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "meal_plan_allergies", joinColumns = @JoinColumn(name = "meal_plan_id"))
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "breakfast_name")),
            @AttributeOverride(name = "cookingTimeMinutes", column = @Column(name = "breakfast_time_minutes")),
            @AttributeOverride(name = "estimatedCost", column = @Column(name = "breakfast_cost")),
            @AttributeOverride(name = "description", column = @Column(name = "breakfast_description", length = 1000))
    })
    private MealDetails breakfast;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "lunch_name")),
            @AttributeOverride(name = "cookingTimeMinutes", column = @Column(name = "lunch_time_minutes")),
            @AttributeOverride(name = "estimatedCost", column = @Column(name = "lunch_cost")),
            @AttributeOverride(name = "description", column = @Column(name = "lunch_description", length = 1000))
    })
    private MealDetails lunch;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "dinner_name")),
            @AttributeOverride(name = "cookingTimeMinutes", column = @Column(name = "dinner_time_minutes")),
            @AttributeOverride(name = "estimatedCost", column = @Column(name = "dinner_cost")),
            @AttributeOverride(name = "description", column = @Column(name = "dinner_description", length = 1000))
    })
    private MealDetails dinner;

    @Column(nullable = false)
    private Double estimatedCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus budgetStatus;

    @Column(length = 2000)
    private String budgetSuggestion;

    @ElementCollection
    @CollectionTable(name = "meal_plan_grocery_items", joinColumns = @JoinColumn(name = "meal_plan_id"))
    private List<GroceryItemEmbeddable> groceryItems = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "meal_plan_substitutes", joinColumns = @JoinColumn(name = "meal_plan_id"))
    private List<SubstituteEmbeddable> substitutes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "meal_plan_timeline", joinColumns = @JoinColumn(name = "meal_plan_id"))
    @Column(name = "timeline_entry", length = 500)
    private List<String> cookingTimeline = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

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

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
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
        this.cuisine = cuisine;
    }

    public CookingTime getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(CookingTime cookingTime) {
        this.cookingTime = cookingTime;
    }

    public List<String> getAvailableIngredients() {
        return availableIngredients;
    }

    public void setAvailableIngredients(List<String> availableIngredients) {
        this.availableIngredients = availableIngredients;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public MealDetails getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(MealDetails breakfast) {
        this.breakfast = breakfast;
    }

    public MealDetails getLunch() {
        return lunch;
    }

    public void setLunch(MealDetails lunch) {
        this.lunch = lunch;
    }

    public MealDetails getDinner() {
        return dinner;
    }

    public void setDinner(MealDetails dinner) {
        this.dinner = dinner;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BudgetStatus getBudgetStatus() {
        return budgetStatus;
    }

    public void setBudgetStatus(BudgetStatus budgetStatus) {
        this.budgetStatus = budgetStatus;
    }

    public String getBudgetSuggestion() {
        return budgetSuggestion;
    }

    public void setBudgetSuggestion(String budgetSuggestion) {
        this.budgetSuggestion = budgetSuggestion;
    }

    public List<GroceryItemEmbeddable> getGroceryItems() {
        return groceryItems;
    }

    public void setGroceryItems(List<GroceryItemEmbeddable> groceryItems) {
        this.groceryItems = groceryItems;
    }

    public List<SubstituteEmbeddable> getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(List<SubstituteEmbeddable> substitutes) {
        this.substitutes = substitutes;
    }

    public List<String> getCookingTimeline() {
        return cookingTimeline;
    }

    public void setCookingTimeline(List<String> cookingTimeline) {
        this.cookingTimeline = cookingTimeline;
    }
}
