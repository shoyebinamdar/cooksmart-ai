import type { SubstituteResponse } from '../types'

interface SubstitutionsProps {
  items: SubstituteResponse[]
}

export function Substitutions({ items }: SubstitutionsProps) {
  if (items.length === 0) {
    return <p className="text-sm text-ink/65">No substitutions needed for this plan.</p>
  }

  return (
    <div className="overflow-hidden border border-line/80 bg-foam/70">
      <div className="grid grid-cols-2 bg-ink px-4 py-3 text-xs font-semibold uppercase tracking-[0.14em] text-gold">
        <span>Original</span>
        <span>Alternative</span>
      </div>
      <ul className="divide-y divide-line/70">
        {items.map((item) => (
          <li key={`${item.ingredient}-${item.alternative}`} className="grid grid-cols-2 px-4 py-3 text-sm">
            <span className="text-ink">{item.ingredient}</span>
            <span className="text-leaf">{item.alternative}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}
