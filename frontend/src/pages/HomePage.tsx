import { useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { generateMealPlan } from '../api/client'
import type { ApiError, MealPlanRequest } from '../types'
import { Header } from '../components/Header'
import { Hero } from '../components/Hero'
import { MealPlanForm } from '../components/MealPlanForm'

export function HomePage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(payload: MealPlanRequest) {
    setLoading(true)
    setError(null)
    try {
      const plan = await generateMealPlan(payload)
      navigate(`/results/${plan.id}`, { state: { plan } })
    } catch (err) {
      const apiError = err as ApiError
      if (apiError.details) {
        const details = Object.values(apiError.details).join(' ')
        setError(details || apiError.message)
      } else {
        setError(apiError.message || 'Unable to generate a meal plan right now.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <Header />
      <Hero />

      <section id="plan" className="relative mx-auto max-w-3xl px-5 py-20 sm:px-8">
        <div className="texture-grid absolute inset-x-0 top-0 -z-10 h-40 opacity-60" aria-hidden />
        <div className="animate-fade-in">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-leaf">Preferences</p>
          <h2 className="mt-3 font-display text-3xl text-ink sm:text-4xl">
            Tell us about your day in the kitchen.
          </h2>
          <p className="mt-3 max-w-2xl text-ink/65">
            We&apos;ll build breakfast, lunch, and dinner around your time, budget, diet, and what
            you already have on hand.
          </p>
        </div>

        <div className="mt-10 border border-line/80 bg-foam/85 p-6 shadow-[0_30px_80px_-40px_rgba(11,61,58,0.5)] backdrop-blur-sm sm:p-8">
          <MealPlanForm onSubmit={handleSubmit} loading={loading} error={error} />
        </div>
      </section>

      <footer className="border-t border-line/60 px-5 py-8 text-center text-sm text-ink/50 sm:px-8">
        CookSmart AI — personal cooking to-do planner
      </footer>
    </div>
  )
}
