package com.cooksmart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cooksmart.config.CookSmartProperties;
import com.cooksmart.dto.request.MealPlanRequest;
import com.cooksmart.dto.response.MealPlanResponse;
import com.cooksmart.entity.MealPlan;
import com.cooksmart.enums.AllergyType;
import com.cooksmart.enums.BudgetStatus;
import com.cooksmart.enums.CookingTime;
import com.cooksmart.enums.CuisineType;
import com.cooksmart.enums.DietType;
import com.cooksmart.exception.ResourceNotFoundException;
import com.cooksmart.repository.MealPlanRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MealPlanServiceTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private AiMealPlannerService aiMealPlannerService;

    @Mock
    private CookSmartProperties properties;

    @InjectMocks
    private MealPlanService mealPlanService;

    @BeforeEach
    void setUp() {
        CookSmartProperties.Currency currency = new CookSmartProperties.Currency();
        currency.setSymbol("₹");
        when(properties.getCurrency()).thenReturn(currency);
    }

    @Test
    void generateMealPlan_persistsAndReturnsWithinBudgetPlan() {
        MealPlanRequest request = sampleRequest(700.0);
        GeneratedMealPlan generated = sampleGenerated(620.0, null);

        when(aiMealPlannerService.generate(any())).thenReturn(generated);
        when(mealPlanRepository.save(any(MealPlan.class))).thenAnswer(invocation -> {
            MealPlan plan = invocation.getArgument(0);
            plan.setId(UUID.randomUUID());
            plan.setCreatedAt(Instant.now());
            return plan;
        });

        MealPlanResponse response = mealPlanService.generateMealPlan(request);

        assertThat(response.getBreakfast().getName()).isEqualTo("Masala Oats");
        assertThat(response.getBudgetAnalysis().getStatus()).isEqualTo("Within Budget");
        assertThat(response.getBudgetAnalysis().getEstimatedCost()).isEqualTo(620.0);
        assertThat(response.getBudgetAnalysis().getSavings()).isEqualTo(80.0);
        assertThat(response.getGroceryList()).hasSize(2);
        assertThat(response.getPlannerSource()).isEqualTo("RULE_BASED");

        ArgumentCaptor<MealPlan> captor = ArgumentCaptor.forClass(MealPlan.class);
        verify(mealPlanRepository).save(captor.capture());
        assertThat(captor.getValue().getBudgetStatus()).isEqualTo(BudgetStatus.WITHIN_BUDGET);
    }

    @Test
    void generateMealPlan_marksOverBudgetAndKeepsSuggestion() {
        MealPlanRequest request = sampleRequest(500.0);
        GeneratedMealPlan generated = sampleGenerated(850.0, "Replace Paneer with Tofu. Replace Salmon with Chicken.");

        when(aiMealPlannerService.generate(any())).thenReturn(generated);
        when(mealPlanRepository.save(any(MealPlan.class))).thenAnswer(invocation -> {
            MealPlan plan = invocation.getArgument(0);
            plan.setId(UUID.randomUUID());
            plan.setCreatedAt(Instant.now());
            return plan;
        });

        MealPlanResponse response = mealPlanService.generateMealPlan(request);

        assertThat(response.getBudgetAnalysis().getStatus()).isEqualTo("Over Budget");
        assertThat(response.getBudgetAnalysis().getSuggestion())
                .contains("Replace Paneer with Tofu");
        assertThat(response.getBudgetAnalysis().getSavings()).isEqualTo(-350.0);
    }

    @Test
    void sanitizeRequest_removesBlankIngredients() {
        MealPlanRequest request = sampleRequest(700.0);
        request.setAvailableIngredients(new ArrayList<>(List.of("Rice", "  ", "", "Eggs")));

        GeneratedMealPlan generated = sampleGenerated(400.0, null);
        when(aiMealPlannerService.generate(any())).thenReturn(generated);
        when(mealPlanRepository.save(any(MealPlan.class))).thenAnswer(invocation -> {
            MealPlan plan = invocation.getArgument(0);
            plan.setId(UUID.randomUUID());
            plan.setCreatedAt(Instant.now());
            return plan;
        });

        mealPlanService.generateMealPlan(request);

        ArgumentCaptor<MealPlanRequest> requestCaptor = ArgumentCaptor.forClass(MealPlanRequest.class);
        verify(aiMealPlannerService).generate(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getAvailableIngredients()).containsExactly("Rice", "Eggs");
    }

    @Test
    void getMealPlan_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(mealPlanRepository.findById(id)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                ResourceNotFoundException.class, () -> mealPlanService.getMealPlan(id));
    }

    private MealPlanRequest sampleRequest(double budget) {
        MealPlanRequest request = new MealPlanRequest();
        request.setPeopleCount(2);
        request.setDiet(DietType.VEGETARIAN);
        request.setCuisine(CuisineType.INDIAN);
        request.setCookingTime(CookingTime.BETWEEN_15_30);
        request.setBudget(budget);
        request.setAvailableIngredients(List.of("Rice", "Onion"));
        request.setAllergies(List.of(AllergyType.NUTS));
        return request;
    }

    private GeneratedMealPlan sampleGenerated(double estimatedCost, String suggestion) {
        GeneratedMealPlan plan = new GeneratedMealPlan();
        plan.setBreakfast(new GeneratedMealPlan.MealDraft("Masala Oats", 15, 90.0, "Savory oats"));
        plan.setLunch(new GeneratedMealPlan.MealDraft("Dal Tadka with Rice", 30, 180.0, "Lentils and rice"));
        plan.setDinner(new GeneratedMealPlan.MealDraft("Chole with Jeera Rice", 40, 200.0, "Chickpea curry"));
        plan.setGroceryList(List.of(
                new GeneratedMealPlan.GroceryDraft("Tomato", "2"),
                new GeneratedMealPlan.GroceryDraft("Lentils", "1 cup")));
        plan.setSubstitutions(List.of(new GeneratedMealPlan.SubstituteDraft("Butter", "Olive Oil")));
        plan.setCookingTimeline(List.of(
                "8:00 AM — Prepare breakfast: Masala Oats",
                "12:00 PM — Prepare lunch: Dal Tadka with Rice",
                "7:00 PM — Prepare dinner: Chole with Jeera Rice"));
        plan.setEstimatedCost(estimatedCost);
        plan.setBudgetSuggestion(suggestion);
        plan.setSource("RULE_BASED");
        return plan;
    }
}
