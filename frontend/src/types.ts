export type DietType = 'Vegetarian' | 'Non-Vegetarian' | 'Vegan'
export type CuisineType = 'Indian' | 'Italian' | 'Chinese' | 'Mexican' | 'Any'
export type CookingTime = '<15 min' | '15–30 min' | '30–60 min'
export type AllergyType = 'Nuts' | 'Dairy' | 'Gluten' | 'Seafood' | 'Other'

export interface MealPlanRequest {
  peopleCount: number
  diet: DietType
  cuisine: CuisineType
  cookingTime: CookingTime
  budget: number
  availableIngredients: string[]
  allergies: AllergyType[]
}

export interface MealResponse {
  name: string
  cookingTimeMinutes: number
  estimatedCost: number
  description: string
}

export interface GroceryItemResponse {
  name: string
  quantity: string
}

export interface SubstituteResponse {
  ingredient: string
  alternative: string
}

export interface BudgetAnalysisResponse {
  estimatedCost: number
  budget: number
  savings: number
  status: string
  suggestion?: string | null
  currencySymbol: string
}

export interface MealPlanResponse {
  id: string
  createdAt: string
  peopleCount: number
  diet: string
  cuisine: string
  cookingTime: string
  breakfast: MealResponse
  lunch: MealResponse
  dinner: MealResponse
  groceryList: GroceryItemResponse[]
  substitutions: SubstituteResponse[]
  budgetAnalysis: BudgetAnalysisResponse
  cookingTimeline: string[]
  plannerSource?: string | null
}

export interface ApiError {
  timestamp?: string
  status: number
  error: string
  message: string
  path?: string
  details?: Record<string, string>
}
