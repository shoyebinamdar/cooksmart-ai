package com.cooksmart.repository;

import com.cooksmart.entity.MealPlan;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
}
