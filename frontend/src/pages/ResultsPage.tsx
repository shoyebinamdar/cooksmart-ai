import { useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router-dom'
import { getMealPlan } from '../api/client'
import type { MealPlanResponse } from '../types'
import { BudgetSummary } from '../components/BudgetSummary'
import { CookingTimeline } from '../components/CookingTimeline'
import { GroceryList } from '../components/GroceryList'
import { MealCard } from '../components/MealCard'
import { Substitutions } from '../components/Substitutions'

export function ResultsPage() {
  const { id } = useParams<{ id: string }>()
  const location = useLocation()
  const initialPlan = (location.state as { plan?: MealPlanResponse } | null)?.plan
  const [plan, setPlan] = useState<MealPlanResponse | null>(initialPlan ?? null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(!initialPlan)

  useEffect(() => {
    if (initialPlan || !id) {
      return
    }
    let cancelled = false
    ;(async () => {
      try {
        const data = await getMealPlan(id)
        if (!cancelled) {
          setPlan(data)
        }
      } catch {
        if (!cancelled) {
          setError('Could not load this meal plan.')
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    })()
    return () => {
      cancelled = true
    }
  }, [id, initialPlan])

  if (loading) {
    return <CenteredMessage title="Loading your plan…" />
  }

  if (error || !plan) {
    return (
      <CenteredMessage
        title="Plan not found"
        body={error ?? 'This meal plan may have expired or never existed.'}
        action
      />
    )
  }

  const currency = plan.budgetAnalysis.currencySymbol || '₹'

  return (
    <div className="min-h-svh">
      <header className="border-b border-line/70 bg-ink text-foam">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-5 py-5 sm:px-8">
          <Link to="/" className="font-display text-2xl tracking-tight text-gold">
            CookSmart
          </Link>
          <Link
            to="/#plan"
            className="text-sm font-medium text-foam/80 transition-colors hover:text-gold"
          >
            Plan another day
          </Link>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-5 py-12 sm:px-8">
        <div className="animate-rise max-w-3xl">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-leaf">Your day</p>
          <h1 className="mt-3 font-display text-4xl text-ink sm:text-5xl">
            Today&apos;s cooking plan
          </h1>
          <p className="mt-3 text-ink/65">
            {plan.peopleCount} {plan.peopleCount === 1 ? 'person' : 'people'} · {plan.diet} ·{' '}
            {plan.cuisine} · {plan.cookingTime}
            {plan.plannerSource ? ` · ${formatSource(plan.plannerSource)}` : ''}
          </p>
        </div>

        <section className="mt-10 grid gap-5 md:grid-cols-3">
          <MealCard
            slot="Breakfast"
            meal={plan.breakfast}
            currencySymbol={currency}
            delayClass="animate-rise"
          />
          <MealCard
            slot="Lunch"
            meal={plan.lunch}
            currencySymbol={currency}
            delayClass="animate-rise-delay-1"
          />
          <MealCard
            slot="Dinner"
            meal={plan.dinner}
            currencySymbol={currency}
            delayClass="animate-rise-delay-2"
          />
        </section>

        <div className="mt-14 grid gap-10 lg:grid-cols-2">
          <section className="animate-fade-in">
            <SectionHeading title="Grocery list" subtitle="Only what you still need to buy." />
            <GroceryList items={plan.groceryList} />
          </section>

          <section className="animate-fade-in">
            <SectionHeading
              title="Ingredient substitutions"
              subtitle="Flexible swaps when something is missing or pricey."
            />
            <Substitutions items={plan.substitutions} />
          </section>
        </div>

        <div className="mt-14 grid gap-10 lg:grid-cols-2">
          <section>
            <SectionHeading title="Budget summary" subtitle="Feasibility at a glance." />
            <BudgetSummary analysis={plan.budgetAnalysis} />
          </section>

          <section>
            <SectionHeading title="Cooking timeline" subtitle="A simple order for the day." />
            <div className="border border-line/80 bg-foam/70 p-6">
              <CookingTimeline entries={plan.cookingTimeline} />
            </div>
          </section>
        </div>
      </main>
    </div>
  )
}

function SectionHeading({ title, subtitle }: { title: string; subtitle: string }) {
  return (
    <div className="mb-5 text-left">
      <h2 className="font-display text-2xl text-ink sm:text-3xl">{title}</h2>
      <p className="mt-1 text-sm text-ink/60">{subtitle}</p>
    </div>
  )
}

function CenteredMessage({
  title,
  body,
  action,
}: {
  title: string
  body?: string
  action?: boolean
}) {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center px-5 text-center">
      <h1 className="font-display text-3xl text-ink">{title}</h1>
      {body && <p className="mt-3 max-w-md text-ink/65">{body}</p>}
      {action && (
        <Link to="/" className="mt-6 bg-ink px-5 py-3 text-sm font-semibold text-foam">
          Back to planner
        </Link>
      )}
    </div>
  )
}

function formatSource(source: string) {
  switch (source) {
    case 'AI':
      return 'AI planned'
    case 'RULE_BASED_FALLBACK':
      return 'Smart fallback'
    default:
      return 'Smart planner'
  }
}
