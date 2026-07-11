import type { BudgetAnalysisResponse } from '../types'

interface BudgetSummaryProps {
  analysis: BudgetAnalysisResponse
}

export function BudgetSummary({ analysis }: BudgetSummaryProps) {
  const withinBudget = analysis.status === 'Within Budget'
  const symbol = analysis.currencySymbol || '₹'

  return (
    <div className="border border-line/80 bg-foam/80 p-6">
      <div className="grid gap-4 sm:grid-cols-3">
        <Stat label="Budget" value={`${symbol}${analysis.budget.toFixed(0)}`} />
        <Stat label="Estimated spend" value={`${symbol}${analysis.estimatedCost.toFixed(0)}`} />
        <Stat
          label={analysis.savings >= 0 ? 'Savings' : 'Over by'}
          value={`${symbol}${Math.abs(analysis.savings).toFixed(0)}`}
        />
      </div>

      <div
        className={`mt-6 border px-4 py-3 text-sm font-semibold ${
          withinBudget
            ? 'border-leaf/30 bg-leaf/10 text-leaf'
            : 'border-coral/30 bg-coral/10 text-coral'
        }`}
      >
        Status: {analysis.status}
      </div>

      {!withinBudget && analysis.suggestion && (
        <p className="mt-4 text-sm leading-relaxed text-ink/75">
          <span className="font-semibold text-ink">Suggestion: </span>
          {analysis.suggestion}
        </p>
      )}
    </div>
  )
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="text-left">
      <p className="text-xs font-semibold uppercase tracking-[0.14em] text-ink/45">{label}</p>
      <p className="mt-1 font-display text-3xl text-ink">{value}</p>
    </div>
  )
}
