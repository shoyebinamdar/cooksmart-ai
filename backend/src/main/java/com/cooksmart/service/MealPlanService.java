package com.cooksmart.service;

import com.cooksmart.config.CookSmartProperties;
import com.cooksmart.dto.request.MealPlanRequest;
import com.cooksmart.dto.response.BudgetAnalysisResponse;
import com.cooksmart.dto.response.GroceryItemResponse;
import com.cooksmart.dto.response.MealPlanResponse;
import com.cooksmart.dto.response.MealResponse;
import com.cooksmart.dto.response.SubstituteResponse;
import com.cooksmart.entity.GroceryItemEmbeddable;
import com.cooksmart.entity.MealDetails;
import com.cooksmart.entity.MealPlan;
import com.cooksmart.entity.SubstituteEmbeddable;
import com.cooksmart.enums.AllergyType;
import com.cooksmart.enums.BudgetStatus;
import com.cooksmart.exception.ResourceNotFoundException;
import com.cooksmart.repository.MealPlanRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealPlanService {

    private static final Logger log = LoggerFactory.getLogger(MealPlanService.class);

    private final MealPlanRepository mealPlanRepository;
    private final AiMealPlannerService aiMealPlannerService;
    private final CookSmartProperties properties;

    public MealPlanService(
            MealPlanRepository mealPlanRepository,
            AiMealPlannerService aiMealPlannerService,
            CookSmartProperties properties) {
        this.mealPlanRepository = mealPlanRepository;
        this.aiMealPlannerService = aiMealPlannerService;
        this.properties = properties;
    }

    @Transactional
    public MealPlanResponse generateMealPlan(MealPlanRequest request) {
        sanitizeRequest(request);
        log.info(
                "Generating meal plan for {} people, diet={}, cuisine={}, budget={}",
                request.getPeopleCount(),
                request.getDiet(),
                request.getCuisine(),
                request.getBudget());

        GeneratedMealPlan generated = aiMealPlannerService.generate(request);
        MealPlan entity = toEntity(request, generated);
        MealPlan saved = mealPlanRepository.save(entity);
        log.info("Meal plan {} persisted with source={}", saved.getId(), generated.getSource());
        return toResponse(saved, generated.getSource());
    }

    @Transactional(readOnly = true)
    public MealPlanResponse getMealPlan(UUID id) {
        MealPlan plan = mealPlanRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found: " + id));
        return toResponse(plan, null);
    }

    @Transactional(readOnly = true)
    public List<MealPlanResponse> listRecent(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return mealPlanRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(safeLimit)
                .map(plan -> toResponse(plan, null))
                .toList();
    }

    void sanitizeRequest(MealPlanRequest request) {
        if (request.getAvailableIngredients() != null) {
            List<String> cleaned = request.getAvailableIngredients().stream()
                    .filter(item -> item != null && !item.isBlank())
                    .map(String::trim)
                    .filter(item -> !item.isEmpty())
                    .distinct()
                    .toList();
            request.setAvailableIngredients(cleaned);
        }
        if (request.getAllergies() == null) {
            request.setAllergies(List.of());
        }
    }

    private MealPlan toEntity(MealPlanRequest request, GeneratedMealPlan generated) {
        double estimatedCost = generated.getEstimatedCost() == null ? 0 : generated.getEstimatedCost();
        BudgetStatus status = estimatedCost <= request.getBudget()
                ? BudgetStatus.WITHIN_BUDGET
                : BudgetStatus.OVER_BUDGET;

        MealPlan plan = new MealPlan();
        plan.setPeopleCount(request.getPeopleCount());
        plan.setBudget(request.getBudget());
        plan.setDiet(request.getDiet());
        plan.setCuisine(request.getCuisine());
        plan.setCookingTime(request.getCookingTime());
        plan.setAvailableIngredients(request.getAvailableIngredients());
        plan.setAllergies(request.getAllergies().stream().map(AllergyType::getLabel).toList());
        plan.setBreakfast(toMealDetails(generated.getBreakfast()));
        plan.setLunch(toMealDetails(generated.getLunch()));
        plan.setDinner(toMealDetails(generated.getDinner()));
        plan.setEstimatedCost(estimatedCost);
        plan.setBudgetStatus(status);
        plan.setBudgetSuggestion(generated.getBudgetSuggestion());
        plan.setGroceryItems(generated.getGroceryList().stream()
                .map(item -> new GroceryItemEmbeddable(item.getName(), item.getQuantity()))
                .collect(Collectors.toList()));
        plan.setSubstitutes(generated.getSubstitutions().stream()
                .map(item -> new SubstituteEmbeddable(item.getIngredient(), item.getAlternative()))
                .collect(Collectors.toList()));
        plan.setCookingTimeline(generated.getCookingTimeline());
        return plan;
    }

    private MealDetails toMealDetails(GeneratedMealPlan.MealDraft draft) {
        if (draft == null) {
            return new MealDetails("Unavailable", 0, 0.0, "");
        }
        return new MealDetails(
                draft.getName(), draft.getCookingTimeMinutes(), draft.getEstimatedCost(), draft.getDescription());
    }

    private MealPlanResponse toResponse(MealPlan plan, String source) {
        MealPlanResponse response = new MealPlanResponse();
        response.setId(plan.getId());
        response.setCreatedAt(plan.getCreatedAt());
        response.setPeopleCount(plan.getPeopleCount());
        response.setDiet(plan.getDiet().getLabel());
        response.setCuisine(plan.getCuisine().getLabel());
        response.setCookingTime(plan.getCookingTime().getLabel());
        response.setBreakfast(toMealResponse(plan.getBreakfast()));
        response.setLunch(toMealResponse(plan.getLunch()));
        response.setDinner(toMealResponse(plan.getDinner()));
        response.setGroceryList(plan.getGroceryItems().stream()
                .map(item -> new GroceryItemResponse(item.getName(), item.getQuantity()))
                .toList());
        response.setSubstitutions(plan.getSubstitutes().stream()
                .map(item -> new SubstituteResponse(item.getIngredient(), item.getAlternative()))
                .toList());
        response.setCookingTimeline(plan.getCookingTimeline());
        response.setPlannerSource(source);

        double savings = Math.round((plan.getBudget() - plan.getEstimatedCost()) * 100.0) / 100.0;
        response.setBudgetAnalysis(new BudgetAnalysisResponse(
                plan.getEstimatedCost(),
                plan.getBudget(),
                savings,
                plan.getBudgetStatus().getLabel(),
                plan.getBudgetSuggestion(),
                properties.getCurrency().getSymbol()));
        return response;
    }

    private MealResponse toMealResponse(MealDetails details) {
        if (details == null) {
            return new MealResponse("Unavailable", 0, 0.0, "");
        }
        return new MealResponse(
                details.getName(),
                details.getCookingTimeMinutes(),
                details.getEstimatedCost(),
                details.getDescription());
    }
}
