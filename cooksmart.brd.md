This is exactly the kind of prompt where a **well-written BRD** will help Cursor generate a much better application than simply pasting the problem statement.

Below is a hackathon-friendly BRD that you can directly give to Cursor.

---

# Business Requirements Document (BRD)

## Project Name

**CookSmart AI – Personal Cooking To-Do Planner**

---

# 1. Problem Statement

Many people struggle to decide what to cook every day while balancing available time, budget, dietary preferences, and available ingredients.

The application should generate an intelligent daily cooking plan that helps users decide what to cook while minimizing effort and unnecessary grocery purchases.

---

# 2. Objective

Develop an AI-powered web application that generates a personalized cooking plan consisting of:

* Breakfast
* Lunch
* Dinner
* Grocery shopping list
* Ingredient substitutions
* Budget feasibility analysis

The application should be simple, responsive, and easy to use.

---

# 3. Target Users

* Students
* Working professionals
* Families
* Anyone planning meals for a day

---

# 4. Functional Requirements

## 4.1 User Inputs

The user should be able to provide:

### Personal Information

* Number of people
* Daily food preference

  * Vegetarian
  * Non-Vegetarian
  * Vegan
* Cuisine preference

  * Indian
  * Italian
  * Chinese
  * Mexican
  * Any

### Cooking Constraints

* Available cooking time

  * <15 min
  * 15–30 min
  * 30–60 min

### Budget

Daily budget in local currency.

### Available Ingredients

User can optionally enter ingredients already available at home.

Example:

```
Rice
Eggs
Tomatoes
Onions
Milk
```

### Allergies (Optional)

* Nuts
* Dairy
* Gluten
* Seafood
* Other

---

## 4.2 AI Meal Planning

The AI should generate:

### Breakfast

* Meal name
* Estimated cooking time
* Estimated cost

### Lunch

* Meal name
* Estimated cooking time
* Estimated cost

### Dinner

* Meal name
* Estimated cooking time
* Estimated cost

---

## 4.3 Grocery List

Generate only missing ingredients.

Example

| Ingredient | Quantity |
| ---------- | -------- |
| Tomato     | 2        |
| Onion      | 1        |
| Bread      | 1 loaf   |

Exclude ingredients already available.

---

## 4.4 Ingredient Substitutions

Provide alternative ingredients.

Example

| Original | Alternative |
| -------- | ----------- |
| Paneer   | Tofu        |
| Butter   | Olive Oil   |
| Cream    | Milk        |

---

## 4.5 Budget Feasibility

Estimate total cost.

Return:

```
Estimated Cost : ₹620

Budget : ₹700

Status : Within Budget
```

OR

```
Estimated Cost : ₹850

Budget : ₹700

Status : Over Budget

Suggestion:
Replace Paneer with Tofu.
Replace Salmon with Chicken.
```

---

## 4.6 Cooking Timeline

Provide a recommended cooking order.

Example

```
8:00 AM

Prepare breakfast

12:00 PM

Prepare lunch

7:00 PM

Prepare dinner
```

---

# 5. Non-Functional Requirements

The application should:

* Be responsive on desktop and mobile
* Generate meal plans in under 5 seconds
* Handle invalid inputs gracefully
* Use secure backend APIs
* Validate all user inputs
* Protect API keys using environment variables

---

# 6. User Flow

```
Landing Page

↓

Enter Preferences

↓

Generate Meal Plan

↓

Display

Breakfast

Lunch

Dinner

↓

View Grocery List

↓

View Substitutions

↓

Budget Analysis
```

---

# 7. Suggested Screens

### Home

Simple form containing user preferences.

---

### Results

Cards displaying

Breakfast

Lunch

Dinner

---

### Grocery List

Checklist of ingredients.

---

### Budget Summary

```
Budget

Estimated Spend

Savings

Status
```

---

# 8. Data Model

## MealPlan

```
id

createdAt

peopleCount

budget

diet

cuisine

availableIngredients

breakfast

lunch

dinner

estimatedCost
```

---

## GroceryItem

```
name

quantity
```

---

## Substitute

```
ingredient

alternative
```

---

# 9. Validation Rules

* Number of people must be greater than 0.
* Budget must be positive.
* Cooking time must be selected.
* Ingredient names cannot be empty.
* Cuisine preference is optional.

---

# 10. Success Criteria

The application should successfully:

* Generate a complete daily meal plan.
* Generate an accurate grocery list.
* Suggest ingredient substitutions.
* Determine whether the plan fits within the user's budget.
* Provide a clean, user-friendly interface.
* Be deployable using Docker with environment variables.

---

# 11. Suggested Tech Stack

Frontend:

* React + Vite
* Tailwind CSS

Backend:

* Spring Boot (or Go Fiber)
* REST APIs

Database:

* PostgreSQL

AI:

* OpenAI-compatible LLM API

Deployment:

* Docker
* Render

---

# 12. Stretch Goals (Optional)

* Weekly meal planner
* Nutritional analysis (Calories, Protein, Carbs, Fat)
* Save favourite meal plans
* Export grocery list as PDF
* Share meal plan via link
* Voice input for ingredients

---
