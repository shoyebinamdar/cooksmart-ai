package com.cooksmart.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.cooksmart.dto.request.MealPlanRequest;
import com.cooksmart.enums.AllergyType;
import com.cooksmart.enums.CookingTime;
import com.cooksmart.enums.CuisineType;
import com.cooksmart.enums.DietType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleBasedMealPlannerServiceTest {

    private RuleBasedMealPlannerService planner;

    @BeforeEach
    void setUp() {
        planner = new RuleBasedMealPlannerService();
    }

    @Test
    void generate_returnsCompletePlanRespectingDietAndAvailableIngredients() {
        MealPlanRequest request = new MealPlanRequest();
        request.setPeopleCount(2);
        request.setDiet(DietType.VEGAN);
        request.setCuisine(CuisineType.INDIAN);
        request.setCookingTime(CookingTime.BETWEEN_30_60);
        request.setBudget(700.0);
        request.setAvailableIngredients(List.of("Rice", "Onion"));
        request.setAllergies(List.of());

        GeneratedMealPlan plan = planner.generate(request);

        assertThat(plan.getBreakfast()).isNotNull();
        assertThat(plan.getLunch()).isNotNull();
        assertThat(plan.getDinner()).isNotNull();
        assertThat(plan.getEstimatedCost()).isPositive();
        assertThat(plan.getCookingTimeline()).hasSize(3);
        assertThat(plan.getSource()).isEqualTo("RULE_BASED");
        assertThat(plan.getGroceryList())
                .noneMatch(item -> item.getName().equalsIgnoreCase("Rice")
                        || item.getName().equalsIgnoreCase("Onion"));
    }

    @Test
    void generate_excludesDairyWhenAllergic() {
        MealPlanRequest request = new MealPlanRequest();
        request.setPeopleCount(1);
        request.setDiet(DietType.VEGETARIAN);
        request.setCuisine(CuisineType.ANY);
        request.setCookingTime(CookingTime.BETWEEN_30_60);
        request.setBudget(500.0);
        request.setAvailableIngredients(List.of());
        request.setAllergies(List.of(AllergyType.DAIRY));

        GeneratedMealPlan plan = planner.generate(request);

        String allNames = (plan.getBreakfast().getName() + plan.getLunch().getName() + plan.getDinner().getName())
                .toLowerCase();
        assertThat(allNames).doesNotContain("paneer", "cheese", "yogurt", "frittata", "quesadilla");
    }

    @Test
    void generate_scalesCostWithPeopleCount() {
        MealPlanRequest one = baseRequest(1);
        MealPlanRequest two = baseRequest(2);

        GeneratedMealPlan planOne = planner.generate(one);
        GeneratedMealPlan planTwo = planner.generate(two);

        assertThat(planTwo.getEstimatedCost()).isGreaterThan(planOne.getEstimatedCost());
    }

    private MealPlanRequest baseRequest(int people) {
        MealPlanRequest request = new MealPlanRequest();
        request.setPeopleCount(people);
        request.setDiet(DietType.VEGETARIAN);
        request.setCuisine(CuisineType.ITALIAN);
        request.setCookingTime(CookingTime.BETWEEN_15_30);
        request.setBudget(800.0);
        request.setAvailableIngredients(List.of());
        request.setAllergies(List.of());
        return request;
    }
}
