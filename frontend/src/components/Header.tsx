import { Link } from 'react-router-dom'

export function Header() {
  return (
    <header className="absolute inset-x-0 top-0 z-20">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-5 py-5 sm:px-8">
        <Link to="/" className="group flex items-center gap-2.5 text-foam">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-gold text-ink transition-transform duration-300 group-hover:rotate-6">
            <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" aria-hidden>
              <path
                d="M6 16c0-5 3-9 6-9s6 4 6 9"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
              />
              <circle cx="12" cy="8" r="1.6" fill="currentColor" />
              <path d="M7 18h10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
            </svg>
          </span>
          <span className="font-display text-xl tracking-tight sm:text-2xl">CookSmart</span>
        </Link>
        <a
          href="#plan"
          className="text-sm font-medium text-foam/85 transition-colors hover:text-gold"
        >
          Plan a day
        </a>
      </div>
    </header>
  )
}
