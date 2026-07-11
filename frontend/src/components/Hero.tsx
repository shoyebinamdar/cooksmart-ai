export function Hero() {
  return (
    <section className="relative min-h-[100svh] overflow-hidden text-foam">
      <div
        className="absolute inset-0 scale-105 bg-cover bg-center"
        style={{
          backgroundImage:
            "linear-gradient(120deg, rgba(11,61,58,0.88) 0%, rgba(11,61,58,0.55) 45%, rgba(11,61,58,0.35) 100%), url('https://images.unsplash.com/photo-1556910103-1c02745aae4d?auto=format&fit=crop&w=2000&q=80')",
        }}
        aria-hidden
      />
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_80%_20%,rgba(232,197,71,0.22),transparent_40%)]" aria-hidden />

      <div className="relative z-10 mx-auto flex min-h-[100svh] max-w-6xl flex-col justify-end px-5 pb-16 pt-28 sm:px-8 sm:pb-20">
        <p className="animate-rise font-display text-5xl leading-none tracking-tight text-gold sm:text-7xl md:text-8xl">
          CookSmart
        </p>
        <h1 className="animate-rise-delay-1 mt-5 max-w-2xl font-display text-3xl leading-tight text-foam sm:text-4xl md:text-5xl">
          One day of meals, planned around your kitchen.
        </h1>
        <p className="animate-rise-delay-2 mt-4 max-w-xl text-base text-foam/80 sm:text-lg">
          Breakfast, lunch, dinner, a grocery list, and budget checks — generated from what you
          already have.
        </p>
        <div className="animate-rise-delay-2 mt-8">
          <a
            href="#plan"
            className="inline-flex items-center gap-2 bg-gold px-6 py-3.5 text-sm font-semibold text-ink transition-transform duration-300 hover:-translate-y-0.5 hover:bg-gold-deep"
          >
            Plan today&apos;s meals
            <span aria-hidden>↓</span>
          </a>
        </div>
      </div>
    </section>
  )
}
