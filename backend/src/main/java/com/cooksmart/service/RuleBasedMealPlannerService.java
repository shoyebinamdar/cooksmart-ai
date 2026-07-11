package com.cooksmart.service;

import com.cooksmart.dto.request.MealPlanRequest;
import com.cooksmart.enums.AllergyType;
import com.cooksmart.enums.CookingTime;
import com.cooksmart.enums.CuisineType;
import com.cooksmart.enums.DietType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedMealPlannerService implements MealPlanner {

    private static final List<MealTemplate> CATALOG = buildCatalog();
    private static final Map<String, String> SUBSTITUTIONS = Map.ofEntries(
            Map.entry("paneer", "Tofu"),
            Map.entry("butter", "Olive Oil"),
            Map.entry("cream", "Milk"),
            Map.entry("milk", "Oat Milk"),
            Map.entry("eggs", "Chickpea Flour Batter"),
            Map.entry("chicken", "Tofu"),
            Map.entry("salmon", "Chicken"),
            Map.entry("shrimp", "Tofu"),
            Map.entry("cheese", "Nutritional Yeast"),
            Map.entry("yogurt", "Coconut Yogurt"),
            Map.entry("wheat flour", "Rice Flour"),
            Map.entry("bread", "Rice Cakes"),
            Map.entry("rice", "Quinoa"),
            Map.entry("soy sauce", "Coconut Aminos"));

    @Override
    public GeneratedMealPlan generate(MealPlanRequest request) {
        List<MealTemplate> candidates = CATALOG.stream()
                .filter(meal -> matchesDiet(meal, request.getDiet()))
                .filter(meal -> matchesCuisine(meal, request.getCuisine()))
                .filter(meal -> matchesTime(meal, request.getCookingTime()))
                .filter(meal -> matchesAllergies(meal, request.getAllergies()))
                .toList();

        if (candidates.isEmpty()) {
            candidates = CATALOG.stream()
                    .filter(meal -> matchesDiet(meal, request.getDiet()))
                    .filter(meal -> matchesAllergies(meal, request.getAllergies()))
                    .toList();
        }

        MealTemplate breakfast = pick(candidates, "BREAKFAST", 0, request);
        MealTemplate lunch = pick(candidates, "LUNCH", 1, request);
        MealTemplate dinner = pick(candidates, "DINNER", 2, request);

        double scale = Math.max(1, request.getPeopleCount());
        GeneratedMealPlan.MealDraft breakfastDraft = toDraft(breakfast, scale);
        GeneratedMealPlan.MealDraft lunchDraft = toDraft(lunch, scale);
        GeneratedMealPlan.MealDraft dinnerDraft = toDraft(dinner, scale);

        Set<String> available = normalizeSet(request.getAvailableIngredients());
        Map<String, String> groceryMap = new LinkedHashMap<>();
        collectGroceries(breakfast, scale, available, groceryMap);
        collectGroceries(lunch, scale, available, groceryMap);
        collectGroceries(dinner, scale, available, groceryMap);

        List<GeneratedMealPlan.GroceryDraft> groceries = groceryMap.entrySet().stream()
                .map(entry -> new GeneratedMealPlan.GroceryDraft(capitalize(entry.getKey()), entry.getValue()))
                .toList();

        List<GeneratedMealPlan.SubstituteDraft> substitutes = buildSubstitutes(
                List.of(breakfast, lunch, dinner), request.getDiet(), request.getAllergies());

        double estimatedCost = round(breakfastDraft.getEstimatedCost()
                + lunchDraft.getEstimatedCost()
                + dinnerDraft.getEstimatedCost());

        GeneratedMealPlan plan = new GeneratedMealPlan();
        plan.setBreakfast(breakfastDraft);
        plan.setLunch(lunchDraft);
        plan.setDinner(dinnerDraft);
        plan.setGroceryList(new ArrayList<>(groceries));
        plan.setSubstitutions(substitutes);
        plan.setCookingTimeline(List.of(
                "8:00 AM — Prepare breakfast: " + breakfastDraft.getName(),
                "12:00 PM — Prepare lunch: " + lunchDraft.getName(),
                "7:00 PM — Prepare dinner: " + dinnerDraft.getName()));
        plan.setEstimatedCost(estimatedCost);
        plan.setBudgetSuggestion(buildBudgetSuggestion(estimatedCost, request.getBudget(), substitutes));
        plan.setSource("RULE_BASED");
        return plan;
    }

    private MealTemplate pick(List<MealTemplate> candidates, String slot, int offset, MealPlanRequest request) {
        List<MealTemplate> slotMeals = candidates.stream()
                .filter(meal -> meal.slots().contains(slot))
                .toList();
        List<MealTemplate> pool = slotMeals.isEmpty() ? candidates : slotMeals;
        if (pool.isEmpty()) {
            return fallbackMeal(slot, request.getDiet());
        }
        int index = Math.floorMod(
                request.getPeopleCount()
                        + request.getBudget().intValue()
                        + request.getDiet().ordinal()
                        + offset * 7,
                pool.size());
        return pool.get(index);
    }

    private GeneratedMealPlan.MealDraft toDraft(MealTemplate template, double peopleScale) {
        GeneratedMealPlan.MealDraft draft = new GeneratedMealPlan.MealDraft(
                template.name(),
                template.minutes(),
                round(template.baseCost() * peopleScale),
                template.description());
        draft.setIngredients(template.ingredients().stream().map(IngredientNeed::name).toList());
        return draft;
    }

    private void collectGroceries(
            MealTemplate meal, double peopleScale, Set<String> available, Map<String, String> groceryMap) {
        for (IngredientNeed need : meal.ingredients()) {
            String key = need.name().toLowerCase(Locale.ROOT);
            if (available.contains(key) || available.stream().anyMatch(a -> key.contains(a) || a.contains(key))) {
                continue;
            }
            String quantity = scaleQuantity(need.quantity(), peopleScale);
            groceryMap.merge(key, quantity, (existing, incoming) -> existing);
        }
    }

    private List<GeneratedMealPlan.SubstituteDraft> buildSubstitutes(
            List<MealTemplate> meals, DietType diet, List<AllergyType> allergies) {
        Set<String> seen = new LinkedHashSet<>();
        List<GeneratedMealPlan.SubstituteDraft> result = new ArrayList<>();
        for (MealTemplate meal : meals) {
            for (IngredientNeed need : meal.ingredients()) {
                String key = need.name().toLowerCase(Locale.ROOT);
                String alternative = SUBSTITUTIONS.get(key);
                if (alternative == null) {
                    continue;
                }
                if (!seen.add(key)) {
                    continue;
                }
                if (diet == DietType.VEGAN && isAnimalProduct(key)) {
                    result.add(new GeneratedMealPlan.SubstituteDraft(capitalize(need.name()), alternative));
                } else if (allergiesContain(allergies, key)) {
                    result.add(new GeneratedMealPlan.SubstituteDraft(capitalize(need.name()), alternative));
                } else if (result.size() < 6) {
                    result.add(new GeneratedMealPlan.SubstituteDraft(capitalize(need.name()), alternative));
                }
            }
        }
        if (result.isEmpty()) {
            result.add(new GeneratedMealPlan.SubstituteDraft("Butter", "Olive Oil"));
            result.add(new GeneratedMealPlan.SubstituteDraft("Cream", "Milk"));
        }
        return result.stream().limit(6).toList();
    }

    private String buildBudgetSuggestion(
            double estimatedCost, double budget, List<GeneratedMealPlan.SubstituteDraft> substitutes) {
        if (estimatedCost <= budget) {
            return null;
        }
        return substitutes.stream()
                .limit(2)
                .map(sub -> "Replace " + sub.getIngredient() + " with " + sub.getAlternative() + ".")
                .collect(Collectors.joining(" "));
    }

    private boolean matchesDiet(MealTemplate meal, DietType diet) {
        return switch (diet) {
            case VEGAN -> meal.diet() == DietType.VEGAN;
            case VEGETARIAN -> meal.diet() == DietType.VEGETARIAN || meal.diet() == DietType.VEGAN;
            case NON_VEGETARIAN -> true;
        };
    }

    private boolean matchesCuisine(MealTemplate meal, CuisineType cuisine) {
        return cuisine == null || cuisine == CuisineType.ANY || meal.cuisine() == cuisine || meal.cuisine() == CuisineType.ANY;
    }

    private boolean matchesTime(MealTemplate meal, CookingTime cookingTime) {
        return meal.minutes() <= cookingTime.getMaxMinutes();
    }

    private boolean matchesAllergies(MealTemplate meal, List<AllergyType> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return true;
        }
        Set<String> banned = new LinkedHashSet<>();
        for (AllergyType allergy : allergies) {
            switch (allergy) {
                case NUTS -> banned.addAll(List.of("nuts", "peanut", "almond", "cashew"));
                case DAIRY -> banned.addAll(List.of("milk", "butter", "cream", "cheese", "paneer", "yogurt"));
                case GLUTEN -> banned.addAll(List.of("bread", "wheat", "pasta", "noodles", "flour"));
                case SEAFOOD -> banned.addAll(List.of("fish", "salmon", "shrimp", "prawn", "seafood"));
                case OTHER -> {
                    // no automatic bans
                }
            }
        }
        return meal.ingredients().stream().noneMatch(need -> banned.stream()
                .anyMatch(ban -> need.name().toLowerCase(Locale.ROOT).contains(ban)));
    }

    private boolean allergiesContain(List<AllergyType> allergies, String ingredient) {
        if (allergies == null) {
            return false;
        }
        for (AllergyType allergy : allergies) {
            if (allergy == AllergyType.DAIRY && isDairy(ingredient)) {
                return true;
            }
            if (allergy == AllergyType.GLUTEN && (ingredient.contains("bread") || ingredient.contains("wheat") || ingredient.contains("pasta"))) {
                return true;
            }
            if (allergy == AllergyType.NUTS && ingredient.contains("nut")) {
                return true;
            }
            if (allergy == AllergyType.SEAFOOD
                    && (ingredient.contains("fish") || ingredient.contains("salmon") || ingredient.contains("shrimp"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnimalProduct(String ingredient) {
        return isDairy(ingredient)
                || ingredient.contains("egg")
                || ingredient.contains("chicken")
                || ingredient.contains("fish")
                || ingredient.contains("salmon")
                || ingredient.contains("shrimp")
                || ingredient.contains("meat");
    }

    private boolean isDairy(String ingredient) {
        return ingredient.contains("milk")
                || ingredient.contains("butter")
                || ingredient.contains("cream")
                || ingredient.contains("cheese")
                || ingredient.contains("paneer")
                || ingredient.contains("yogurt");
    }

    private Set<String> normalizeSet(List<String> values) {
        if (values == null) {
            return Set.of();
        }
        return values.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(v -> v.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String scaleQuantity(String quantity, double peopleScale) {
        if (peopleScale <= 1.0) {
            return quantity;
        }
        String[] parts = quantity.trim().split(" ", 2);
        try {
            double numeric = Double.parseDouble(parts[0]);
            double scaled = Math.ceil(numeric * peopleScale);
            String unit = parts.length > 1 ? " " + parts[1] : "";
            return ((int) scaled) + unit;
        } catch (NumberFormatException ex) {
            return quantity + (peopleScale > 1 ? " (x" + ((int) peopleScale) + ")" : "");
        }
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private MealTemplate fallbackMeal(String slot, DietType diet) {
        return switch (slot) {
            case "BREAKFAST" -> new MealTemplate(
                    "Quick Oats Bowl",
                    DietType.VEGAN,
                    CuisineType.ANY,
                    Set.of("BREAKFAST"),
                    10,
                    80,
                    "Warm oats with fruit — ready in minutes.",
                    List.of(new IngredientNeed("oats", "1 cup"), new IngredientNeed("banana", "1"), new IngredientNeed("milk", "1 cup")));
            case "LUNCH" -> new MealTemplate(
                    "Veggie Rice Bowl",
                    diet == DietType.NON_VEGETARIAN ? DietType.VEGETARIAN : diet,
                    CuisineType.ANY,
                    Set.of("LUNCH"),
                    25,
                    150,
                    "Simple rice bowl with seasonal vegetables.",
                    List.of(new IngredientNeed("rice", "1 cup"), new IngredientNeed("vegetables", "2 cups"), new IngredientNeed("oil", "1 tbsp")));
            default -> new MealTemplate(
                    "Lentil Soup",
                    DietType.VEGAN,
                    CuisineType.ANY,
                    Set.of("DINNER"),
                    30,
                    140,
                    "Comforting lentil soup for dinner.",
                    List.of(new IngredientNeed("lentils", "1 cup"), new IngredientNeed("onion", "1"), new IngredientNeed("tomato", "2")));
        };
    }

    private static List<MealTemplate> buildCatalog() {
        List<MealTemplate> meals = new ArrayList<>();

        // Indian
        meals.add(meal("Masala Oats", DietType.VEGETARIAN, CuisineType.INDIAN, "BREAKFAST", 15, 90,
                "Savory oats with spices and vegetables.",
                List.of(i("oats", "1 cup"), i("onion", "1"), i("tomato", "1"), i("spices", "1 tsp"))));
        meals.add(meal("Poha", DietType.VEGETARIAN, CuisineType.INDIAN, "BREAKFAST", 20, 100,
                "Flattened rice with peanuts, onion, and lemon.",
                List.of(i("poha", "2 cups"), i("onion", "1"), i("peanuts", "2 tbsp"), i("lemon", "1"))));
        meals.add(meal("Besan Chilla", DietType.VEGAN, CuisineType.INDIAN, "BREAKFAST", 20, 85,
                "Chickpea flour pancakes with herbs.",
                List.of(i("chickpea flour", "1 cup"), i("onion", "1"), i("tomato", "1"), i("oil", "1 tbsp"))));
        meals.add(meal("Dal Tadka with Rice", DietType.VEGAN, CuisineType.INDIAN, "LUNCH", 35, 180,
                "Tempered lentils served with steamed rice.",
                List.of(i("lentils", "1 cup"), i("rice", "1 cup"), i("onion", "1"), i("tomato", "2"), i("spices", "1 tsp"))));
        meals.add(meal("Paneer Bhurji Wrap", DietType.VEGETARIAN, CuisineType.INDIAN, "LUNCH", 25, 220,
                "Spiced scrambled paneer in a soft wrap.",
                List.of(i("paneer", "200 g"), i("onion", "1"), i("tomato", "2"), i("bread", "2"))));
        meals.add(meal("Chicken Curry with Roti", DietType.NON_VEGETARIAN, CuisineType.INDIAN, "DINNER", 45, 320,
                "Home-style chicken curry with fresh roti.",
                List.of(i("chicken", "400 g"), i("onion", "2"), i("tomato", "3"), i("wheat flour", "2 cups"), i("spices", "2 tsp"))));
        meals.add(meal("Chole with Jeera Rice", DietType.VEGAN, CuisineType.INDIAN, "DINNER", 40, 200,
                "Chickpea curry with cumin rice.",
                List.of(i("chickpeas", "2 cups"), i("rice", "1 cup"), i("onion", "2"), i("tomato", "2"), i("spices", "2 tsp"))));
        meals.add(meal("Vegetable Khichdi", DietType.VEGETARIAN, CuisineType.INDIAN, "DINNER", 30, 160,
                "One-pot rice and lentil comfort meal.",
                List.of(i("rice", "1 cup"), i("lentils", "0.5 cup"), i("vegetables", "2 cups"), i("ghee", "1 tbsp"))));

        // Italian
        meals.add(meal("Tomato Bruschetta", DietType.VEGAN, CuisineType.ITALIAN, "BREAKFAST", 15, 110,
                "Toasted bread topped with fresh tomatoes and basil.",
                List.of(i("bread", "4 slices"), i("tomato", "3"), i("basil", "1 bunch"), i("olive oil", "2 tbsp"))));
        meals.add(meal("Veggie Frittata", DietType.VEGETARIAN, CuisineType.ITALIAN, "BREAKFAST", 25, 160,
                "Baked egg dish with seasonal vegetables.",
                List.of(i("eggs", "4"), i("cheese", "50 g"), i("spinach", "2 cups"), i("onion", "1"))));
        meals.add(meal("Pesto Pasta", DietType.VEGETARIAN, CuisineType.ITALIAN, "LUNCH", 25, 210,
                "Pasta tossed with basil pesto and cherry tomatoes.",
                List.of(i("pasta", "300 g"), i("basil", "1 bunch"), i("olive oil", "3 tbsp"), i("tomato", "2"), i("cheese", "40 g"))));
        meals.add(meal("Margherita Flatbread", DietType.VEGETARIAN, CuisineType.ITALIAN, "LUNCH", 30, 230,
                "Crispy flatbread with tomato, mozzarella, and basil.",
                List.of(i("bread", "2"), i("tomato", "2"), i("cheese", "100 g"), i("basil", "1 bunch"))));
        meals.add(meal("Garlic Butter Shrimp Pasta", DietType.NON_VEGETARIAN, CuisineType.ITALIAN, "DINNER", 30, 380,
                "Quick shrimp pasta in garlic butter sauce.",
                List.of(i("shrimp", "300 g"), i("pasta", "300 g"), i("butter", "2 tbsp"), i("garlic", "4 cloves"), i("cream", "0.5 cup"))));
        meals.add(meal("Mushroom Risotto", DietType.VEGETARIAN, CuisineType.ITALIAN, "DINNER", 40, 260,
                "Creamy arborio rice with sautéed mushrooms.",
                List.of(i("rice", "1.5 cups"), i("mushrooms", "250 g"), i("onion", "1"), i("butter", "2 tbsp"), i("cheese", "50 g"))));

        // Chinese
        meals.add(meal("Veggie Congee", DietType.VEGAN, CuisineType.CHINESE, "BREAKFAST", 30, 100,
                "Comforting rice porridge with ginger and scallions.",
                List.of(i("rice", "0.5 cup"), i("ginger", "1 inch"), i("scallions", "2"), i("soy sauce", "1 tbsp"))));
        meals.add(meal("Egg Fried Rice", DietType.VEGETARIAN, CuisineType.CHINESE, "LUNCH", 20, 170,
                "Classic fried rice with eggs and mixed vegetables.",
                List.of(i("rice", "2 cups"), i("eggs", "2"), i("vegetables", "2 cups"), i("soy sauce", "2 tbsp"), i("oil", "2 tbsp"))));
        meals.add(meal("Tofu Stir Fry", DietType.VEGAN, CuisineType.CHINESE, "LUNCH", 25, 190,
                "Crispy tofu with colorful vegetables in light sauce.",
                List.of(i("tofu", "300 g"), i("vegetables", "3 cups"), i("soy sauce", "2 tbsp"), i("garlic", "3 cloves"), i("oil", "2 tbsp"))));
        meals.add(meal("Chicken Noodles", DietType.NON_VEGETARIAN, CuisineType.CHINESE, "DINNER", 30, 280,
                "Wok-tossed noodles with chicken and veggies.",
                List.of(i("noodles", "300 g"), i("chicken", "250 g"), i("vegetables", "2 cups"), i("soy sauce", "2 tbsp"), i("oil", "2 tbsp"))));
        meals.add(meal("Vegetable Manchurian", DietType.VEGAN, CuisineType.CHINESE, "DINNER", 35, 210,
                "Crispy veggie balls in tangy Manchurian sauce.",
                List.of(i("vegetables", "3 cups"), i("flour", "0.5 cup"), i("soy sauce", "2 tbsp"), i("garlic", "4 cloves"), i("onion", "1"))));

        // Mexican
        meals.add(meal("Avocado Toast with Salsa", DietType.VEGAN, CuisineType.MEXICAN, "BREAKFAST", 10, 140,
                "Toasted bread with smashed avocado and fresh salsa.",
                List.of(i("bread", "2 slices"), i("avocado", "1"), i("tomato", "1"), i("onion", "0.5"), i("lime", "1"))));
        meals.add(meal("Bean Breakfast Burrito", DietType.VEGETARIAN, CuisineType.MEXICAN, "BREAKFAST", 20, 180,
                "Warm tortilla filled with beans, eggs, and salsa.",
                List.of(i("tortilla", "2"), i("beans", "1 cup"), i("eggs", "2"), i("cheese", "40 g"), i("tomato", "1"))));
        meals.add(meal("Veggie Quesadilla", DietType.VEGETARIAN, CuisineType.MEXICAN, "LUNCH", 20, 200,
                "Crispy quesadilla stuffed with veggies and cheese.",
                List.of(i("tortilla", "2"), i("cheese", "100 g"), i("vegetables", "2 cups"), i("onion", "1"))));
        meals.add(meal("Chicken Tacos", DietType.NON_VEGETARIAN, CuisineType.MEXICAN, "DINNER", 30, 300,
                "Soft tacos with spiced chicken and fresh toppings.",
                List.of(i("tortilla", "6"), i("chicken", "350 g"), i("onion", "1"), i("tomato", "2"), i("lime", "2"), i("cheese", "50 g"))));
        meals.add(meal("Black Bean Bowl", DietType.VEGAN, CuisineType.MEXICAN, "DINNER", 25, 190,
                "Rice bowl with black beans, corn, and salsa.",
                List.of(i("rice", "1 cup"), i("beans", "1.5 cups"), i("corn", "1 cup"), i("tomato", "2"), i("avocado", "1"), i("lime", "1"))));

        // Any / flexible
        meals.add(meal("Greek Yogurt Parfait", DietType.VEGETARIAN, CuisineType.ANY, "BREAKFAST", 10, 120,
                "Layered yogurt with fruit and oats.",
                List.of(i("yogurt", "1 cup"), i("oats", "0.5 cup"), i("banana", "1"), i("berries", "0.5 cup"))));
        meals.add(meal("Grilled Cheese & Tomato Soup", DietType.VEGETARIAN, CuisineType.ANY, "LUNCH", 25, 180,
                "Classic comfort combo for a quick lunch.",
                List.of(i("bread", "4 slices"), i("cheese", "80 g"), i("tomato", "4"), i("butter", "1 tbsp"), i("cream", "0.25 cup"))));
        meals.add(meal("Salmon with Veggies", DietType.NON_VEGETARIAN, CuisineType.ANY, "DINNER", 35, 420,
                "Pan-seared salmon with roasted vegetables.",
                List.of(i("salmon", "350 g"), i("vegetables", "3 cups"), i("olive oil", "2 tbsp"), i("lemon", "1"), i("garlic", "3 cloves"))));
        meals.add(meal("Chickpea Salad Bowl", DietType.VEGAN, CuisineType.ANY, "LUNCH", 15, 150,
                "Protein-packed salad with lemon dressing.",
                List.of(i("chickpeas", "1.5 cups"), i("cucumber", "1"), i("tomato", "2"), i("onion", "0.5"), i("olive oil", "2 tbsp"), i("lemon", "1"))));

        return List.copyOf(meals);
    }

    private static MealTemplate meal(
            String name,
            DietType diet,
            CuisineType cuisine,
            String slot,
            int minutes,
            double baseCost,
            String description,
            List<IngredientNeed> ingredients) {
        return new MealTemplate(name, diet, cuisine, Set.of(slot), minutes, baseCost, description, ingredients);
    }

    private static IngredientNeed i(String name, String quantity) {
        return new IngredientNeed(name, quantity);
    }

    private record IngredientNeed(String name, String quantity) {
    }

    private record MealTemplate(
            String name,
            DietType diet,
            CuisineType cuisine,
            Set<String> slots,
            int minutes,
            double baseCost,
            String description,
            List<IngredientNeed> ingredients) {
    }
}
