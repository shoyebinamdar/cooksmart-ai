import type { MealResponse } from '../types'

const MEAL_ACCENTS = {
  Breakfast: 'from-[#2f6f5e]/to-[#1a524e]',
  Lunch: 'from-[#0b3d3a]/to-[#2f6f5e]',
  Dinner: 'from-[#1a524e]/to-[#0b3d3a]',
} as const

interface MealCardProps {
  slot: keyof typeof MEAL_ACCENTS
  meal: MealResponse
  currencySymbol: string
  delayClass?: string
}

export function MealCard({ slot, meal, currencySymbol, delayClass = '' }: MealCardProps) {
  return (
    <article
      className={`overflow-hidden border border-line/80 bg-foam/80 shadow-[0_20px_50px_-30px_rgba(11,61,58,0.45)] backdrop-blur-sm ${delayClass}`}
    >
      <div className={`bg-gradient-to-br ${MEAL_ACCENTS[slot]} px-5 py-4 text-foam`}>
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-gold">{slot}</p>
        <h3 className="mt-2 font-display text-2xl leading-tight">{meal.name}</h3>
      </div>
      <div className="space-y-3 px-5 py-5 text-left">
        <p className="text-sm leading-relaxed text-ink/70">{meal.description}</p>
        <div className="flex flex-wrap gap-4 text-sm font-medium text-ink">
          <span>{meal.cookingTimeMinutes} min</span>
          <span>
            {currencySymbol}
            {meal.estimatedCost.toFixed(0)}
          </span>
        </div>
      </div>
    </article>
  )
}
