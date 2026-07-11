interface TimelineProps {
  entries: string[]
}

export function CookingTimeline({ entries }: TimelineProps) {
  return (
    <ol className="relative space-y-0 border-l-2 border-gold/70 pl-6">
      {entries.map((entry, index) => {
        const [time, ...rest] = entry.split('—')
        const detail = rest.join('—').trim() || entry
        return (
          <li key={entry} className="relative pb-8 last:pb-0">
            <span className="absolute -left-[1.9rem] top-1 h-3.5 w-3.5 rounded-sm bg-gold" />
            <p className="text-xs font-semibold uppercase tracking-[0.16em] text-leaf">
              {time?.trim() || `Step ${index + 1}`}
            </p>
            <p className="mt-1 text-sm text-ink/80">{detail}</p>
          </li>
        )
      })}
    </ol>
  )
}
