package com.cooksmart.controller;

import com.cooksmart.dto.request.MealPlanRequest;
import com.cooksmart.dto.response.MealPlanResponse;
import com.cooksmart.service.MealPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meal-plans")
@Tag(name = "Meal Plans", description = "Generate and retrieve personalized cooking plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    @Operation(summary = "Generate a daily meal plan")
    public ResponseEntity<MealPlanResponse> generate(@Valid @RequestBody MealPlanRequest request) {
        MealPlanResponse response = mealPlanService.generateMealPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a meal plan by ID")
    public ResponseEntity<MealPlanResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mealPlanService.getMealPlan(id));
    }

    @GetMapping
    @Operation(summary = "List recent meal plans")
    public ResponseEntity<List<MealPlanResponse>> listRecent(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(mealPlanService.listRecent(limit));
    }
}
