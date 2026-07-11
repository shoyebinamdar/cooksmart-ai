package com.cooksmart.service;

import com.cooksmart.dto.request.MealPlanRequest;

public interface MealPlanner {

    GeneratedMealPlan generate(MealPlanRequest request);
}
