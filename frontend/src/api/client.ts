import type { ApiError, MealPlanRequest, MealPlanResponse } from '../types'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

async function parseError(response: Response): Promise<ApiError> {
  try {
    return (await response.json()) as ApiError
  } catch {
    return {
      status: response.status,
      error: response.statusText || 'Error',
      message: 'Something went wrong. Please try again.',
    }
  }
}

export async function generateMealPlan(payload: MealPlanRequest): Promise<MealPlanResponse> {
  const response = await fetch(`${API_BASE}/api/v1/meal-plans`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    throw await parseError(response)
  }

  return (await response.json()) as MealPlanResponse
}

export async function getMealPlan(id: string): Promise<MealPlanResponse> {
  const response = await fetch(`${API_BASE}/api/v1/meal-plans/${id}`)
  if (!response.ok) {
    throw await parseError(response)
  }
  return (await response.json()) as MealPlanResponse
}

export async function checkHealth(): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE}/api/v1/health`)
    return response.ok
  } catch {
    return false
  }
}
