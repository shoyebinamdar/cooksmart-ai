package com.cooksmart.service;

import com.cooksmart.dto.request.MealPlanRequest;
import com.cooksmart.config.CookSmartProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class AiMealPlannerService implements MealPlanner {

    private static final Logger log = LoggerFactory.getLogger(AiMealPlannerService.class);

    private final CookSmartProperties properties;
    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;
    private final RuleBasedMealPlannerService fallbackPlanner;

    public AiMealPlannerService(
            CookSmartProperties properties,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            RuleBasedMealPlannerService fallbackPlanner) {
        this.properties = properties;
        this.restClientBuilder = restClientBuilder;
        this.objectMapper = objectMapper;
        this.fallbackPlanner = fallbackPlanner;
    }

    @Override
    public GeneratedMealPlan generate(MealPlanRequest request) {
        if (!properties.getAi().isConfigured()) {
            log.info("AI planner not configured; using rule-based planner");
            return fallbackPlanner.generate(request);
        }

        try {
            String content = callLlm(request);
            GeneratedMealPlan plan = parsePlan(content);
            plan.setSource("AI");
            if (plan.getEstimatedCost() == null) {
                double total = safeCost(plan.getBreakfast()) + safeCost(plan.getLunch()) + safeCost(plan.getDinner());
                plan.setEstimatedCost(Math.round(total * 100.0) / 100.0);
            }
            if (plan.getCookingTimeline() == null || plan.getCookingTimeline().isEmpty()) {
                plan.setCookingTimeline(List.of(
                        "8:00 AM — Prepare breakfast: " + plan.getBreakfast().getName(),
                        "12:00 PM — Prepare lunch: " + plan.getLunch().getName(),
                        "7:00 PM — Prepare dinner: " + plan.getDinner().getName()));
            }
            log.info("AI meal plan generated successfully");
            return plan;
        } catch (Exception ex) {
            log.warn("AI planner failed, falling back to rule-based planner: {}", ex.getMessage());
            GeneratedMealPlan fallback = fallbackPlanner.generate(request);
            fallback.setSource("RULE_BASED_FALLBACK");
            return fallback;
        }
    }

    private String callLlm(MealPlanRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", properties.getAi().getModel());
        body.put("temperature", 0.4);
        body.put("response_format", Map.of("type", "json_object"));
        body.put(
                "messages",
                List.of(
                        Map.of("role", "system", "content", systemPrompt()),
                        Map.of("role", "user", "content", userPrompt(request))));

        RestClient client = restClientBuilder
                .baseUrl(trimTrailingSlash(properties.getAi().getBaseUrl()))
                .build();

        String response = client.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + properties.getAi().getApiKey())
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").path(0).path("message").path("content").asText();
        } catch (Exception ex) {
            throw new RestClientException("Failed to parse LLM response", ex);
        }
    }

    private GeneratedMealPlan parsePlan(String content) throws Exception {
        JsonNode root = objectMapper.readTree(content);
        GeneratedMealPlan plan = new GeneratedMealPlan();
        plan.setBreakfast(readMeal(root.path("breakfast")));
        plan.setLunch(readMeal(root.path("lunch")));
        plan.setDinner(readMeal(root.path("dinner")));

        List<GeneratedMealPlan.GroceryDraft> groceries = new ArrayList<>();
        for (JsonNode node : root.path("groceryList")) {
            groceries.add(new GeneratedMealPlan.GroceryDraft(
                    node.path("name").asText(), node.path("quantity").asText("1")));
        }
        plan.setGroceryList(groceries);

        List<GeneratedMealPlan.SubstituteDraft> substitutes = new ArrayList<>();
        for (JsonNode node : root.path("substitutions")) {
            substitutes.add(new GeneratedMealPlan.SubstituteDraft(
                    node.path("ingredient").asText(), node.path("alternative").asText()));
        }
        plan.setSubstitutions(substitutes);

        List<String> timeline = new ArrayList<>();
        for (JsonNode node : root.path("cookingTimeline")) {
            timeline.add(node.asText());
        }
        plan.setCookingTimeline(timeline);

        if (root.has("estimatedCost")) {
            plan.setEstimatedCost(root.path("estimatedCost").asDouble());
        }
        if (root.has("budgetSuggestion") && !root.path("budgetSuggestion").isNull()) {
            plan.setBudgetSuggestion(root.path("budgetSuggestion").asText(null));
        }
        return plan;
    }

    private GeneratedMealPlan.MealDraft readMeal(JsonNode node) {
        GeneratedMealPlan.MealDraft meal = new GeneratedMealPlan.MealDraft(
                node.path("name").asText("Meal"),
                node.path("cookingTimeMinutes").asInt(20),
                node.path("estimatedCost").asDouble(100),
                node.path("description").asText(""));
        List<String> ingredients = new ArrayList<>();
        for (JsonNode ingredient : node.path("ingredients")) {
            ingredients.add(ingredient.asText());
        }
        meal.setIngredients(ingredients);
        return meal;
    }

    private String systemPrompt() {
        return """
                You are CookSmart AI, a meal planning assistant.
                Return ONLY valid JSON with this schema:
                {
                  "breakfast": {"name":"", "cookingTimeMinutes":0, "estimatedCost":0, "description":"", "ingredients":[""]},
                  "lunch": {"name":"", "cookingTimeMinutes":0, "estimatedCost":0, "description":"", "ingredients":[""]},
                  "dinner": {"name":"", "cookingTimeMinutes":0, "estimatedCost":0, "description":"", "ingredients":[""]},
                  "groceryList": [{"name":"", "quantity":""}],
                  "substitutions": [{"ingredient":"", "alternative":""}],
                  "cookingTimeline": ["8:00 AM — Prepare breakfast", "12:00 PM — Prepare lunch", "7:00 PM — Prepare dinner"],
                  "estimatedCost": 0,
                  "budgetSuggestion": null
                }
                Rules:
                - Respect diet, cuisine, cooking time, allergies, and available ingredients.
                - groceryList must exclude ingredients the user already has.
                - Costs should be realistic for the local currency and people count.
                - If over budget, set budgetSuggestion with concrete cheaper swaps.
                """;
    }

    private String userPrompt(MealPlanRequest request) {
        String ingredients = request.getAvailableIngredients() == null || request.getAvailableIngredients().isEmpty()
                ? "none"
                : String.join(", ", request.getAvailableIngredients());
        String allergies = request.getAllergies() == null || request.getAllergies().isEmpty()
                ? "none"
                : request.getAllergies().stream().map(Enum::name).collect(Collectors.joining(", "));
        return """
                People: %d
                Diet: %s
                Cuisine: %s
                Max cooking time: %s
                Daily budget: %.2f
                Available ingredients: %s
                Allergies: %s
                Generate a complete daily cooking plan.
                """
                .formatted(
                        request.getPeopleCount(),
                        request.getDiet().getLabel(),
                        request.getCuisine().getLabel(),
                        request.getCookingTime().getLabel(),
                        request.getBudget(),
                        ingredients,
                        allergies);
    }

    private double safeCost(GeneratedMealPlan.MealDraft meal) {
        return meal == null || meal.getEstimatedCost() == null ? 0 : meal.getEstimatedCost();
    }

    private String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
