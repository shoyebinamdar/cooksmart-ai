import { useState } from 'react'
import type { GroceryItemResponse } from '../types'

interface GroceryListProps {
  items: GroceryItemResponse[]
}

export function GroceryList({ items }: GroceryListProps) {
  const [checked, setChecked] = useState<Record<string, boolean>>({})

  if (items.length === 0) {
    return (
      <p className="text-sm text-ink/65">
        You already have everything you need for today&apos;s plan.
      </p>
    )
  }

  return (
    <ul className="divide-y divide-line/70 border border-line/80 bg-foam/70">
      {items.map((item) => {
        const key = `${item.name}-${item.quantity}`
        const isChecked = Boolean(checked[key])
        return (
          <li key={key}>
            <label className="flex cursor-pointer items-center gap-3 px-4 py-3.5 transition-colors hover:bg-mist/60">
              <input
                type="checkbox"
                checked={isChecked}
                onChange={() => setChecked((prev) => ({ ...prev, [key]: !prev[key] }))}
                className="h-4 w-4 accent-ink"
              />
              <span className={`flex-1 text-sm ${isChecked ? 'text-ink/40 line-through' : 'text-ink'}`}>
                {item.name}
              </span>
              <span className="text-sm text-ink/60">{item.quantity}</span>
            </label>
          </li>
        )
      })}
    </ul>
  )
}
