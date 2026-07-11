import { useState, type FormEvent, type ReactNode } from 'react'
import type {
  AllergyType,
  CookingTime,
  CuisineType,
  DietType,
  MealPlanRequest,
} from '../types'

const DIETS: DietType[] = ['Vegetarian', 'Non-Vegetarian', 'Vegan']
const CUISINES: CuisineType[] = ['Indian', 'Italian', 'Chinese', 'Mexican', 'Any']
const TIMES: CookingTime[] = ['<15 min', '15–30 min', '30–60 min']
const ALLERGIES: AllergyType[] = ['Nuts', 'Dairy', 'Gluten', 'Seafood', 'Other']

interface MealPlanFormProps {
  onSubmit: (payload: MealPlanRequest) => Promise<void>
  loading: boolean
  error: string | null
}

export function MealPlanForm({ onSubmit, loading, error }: MealPlanFormProps) {
  const [peopleCount, setPeopleCount] = useState(2)
  const [diet, setDiet] = useState<DietType>('Vegetarian')
  const [cuisine, setCuisine] = useState<CuisineType>('Indian')
  const [cookingTime, setCookingTime] = useState<CookingTime>('15–30 min')
  const [budget, setBudget] = useState(700)
  const [ingredientsText, setIngredientsText] = useState('Rice\nEggs\nTomatoes\nOnions\nMilk')
  const [allergies, setAllergies] = useState<AllergyType[]>([])

  function toggleAllergy(allergy: AllergyType) {
    setAllergies((prev) =>
      prev.includes(allergy) ? prev.filter((item) => item !== allergy) : [...prev, allergy],
    )
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const availableIngredients = ingredientsText
      .split(/[\n,]/)
      .map((item) => item.trim())
      .filter(Boolean)

    await onSubmit({
      peopleCount,
      diet,
      cuisine,
      cookingTime,
      budget,
      availableIngredients,
      allergies,
    })
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-8">
      <div className="grid gap-6 sm:grid-cols-2">
        <Field label="Number of people">
          <input
            type="number"
            min={1}
            max={20}
            required
            value={peopleCount}
            onChange={(e) => setPeopleCount(Number(e.target.value))}
            className="field-input"
          />
        </Field>

        <Field label="Daily budget (₹)">
          <input
            type="number"
            min={1}
            step={1}
            required
            value={budget}
            onChange={(e) => setBudget(Number(e.target.value))}
            className="field-input"
          />
        </Field>
      </div>

      <Field label="Food preference">
        <div className="flex flex-wrap gap-2">
          {DIETS.map((option) => (
            <ChoiceChip
              key={option}
              selected={diet === option}
              onClick={() => setDiet(option)}
              label={option}
            />
          ))}
        </div>
      </Field>

      <Field label="Cuisine preference">
        <div className="flex flex-wrap gap-2">
          {CUISINES.map((option) => (
            <ChoiceChip
              key={option}
              selected={cuisine === option}
              onClick={() => setCuisine(option)}
              label={option}
            />
          ))}
        </div>
      </Field>

      <Field label="Available cooking time">
        <div className="flex flex-wrap gap-2">
          {TIMES.map((option) => (
            <ChoiceChip
              key={option}
              selected={cookingTime === option}
              onClick={() => setCookingTime(option)}
              label={option}
            />
          ))}
        </div>
      </Field>

      <Field label="Ingredients at home (optional)" hint="One per line or comma-separated">
        <textarea
          rows={5}
          value={ingredientsText}
          onChange={(e) => setIngredientsText(e.target.value)}
          className="field-input resize-y"
          placeholder="Rice&#10;Eggs&#10;Tomatoes"
        />
      </Field>

      <Field label="Allergies (optional)">
        <div className="flex flex-wrap gap-2">
          {ALLERGIES.map((option) => (
            <ChoiceChip
              key={option}
              selected={allergies.includes(option)}
              onClick={() => toggleAllergy(option)}
              label={option}
            />
          ))}
        </div>
      </Field>

      {error && (
        <div
          role="alert"
          className="border border-coral/40 bg-coral/10 px-4 py-3 text-sm text-coral"
        >
          {error}
        </div>
      )}

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-ink px-6 py-4 text-sm font-semibold tracking-wide text-foam transition-all duration-300 hover:bg-ink-soft disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
      >
        {loading ? 'Crafting your plan…' : 'Generate meal plan'}
      </button>
    </form>
  )
}

function Field({
  label,
  hint,
  children,
}: {
  label: string
  hint?: string
  children: ReactNode
}) {
  return (
    <label className="block space-y-2 text-left">
      <span className="text-sm font-semibold text-ink">{label}</span>
      {hint && <span className="block text-xs text-ink/55">{hint}</span>}
      {children}
    </label>
  )
}

function ChoiceChip({
  label,
  selected,
  onClick,
}: {
  label: string
  selected: boolean
  onClick: () => void
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`border px-3.5 py-2 text-sm transition-colors duration-200 ${
        selected
          ? 'border-ink bg-ink text-foam'
          : 'border-line bg-foam/70 text-ink hover:border-ink/40'
      }`}
    >
      {label}
    </button>
  )
}
