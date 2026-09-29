import { ArrowUpRight, Sun } from "lucide-react";
import { Link } from "react-router-dom";

export const AUTH_INPUT_CLASS =
  "w-full rounded border-2 border-[var(--color-bark)] bg-[var(--color-white)] px-3.5 py-3 text-sm text-[var(--color-bark-dark)] placeholder:text-[var(--color-graph)]/70 outline-none focus:border-[var(--color-sand)] focus:ring-2 focus:ring-[var(--color-sand)]";

export function AuthField({ id, label, error, children }) {
  return (
    <div className="min-w-0">
      <label
        htmlFor={id}
        className="mb-1.5 block text-sm font-semibold text-[var(--color-bark-dark)]"
      >
        {label}
      </label>
      {children}
      {error && (
        <p id={`${id}-error`} className="mt-1 text-xs text-red-700">
          {error}
        </p>
      )}
    </div>
  );
}

export default function AuthLayout({
  eyebrow,
  title,
  description,
  alternateText,
  alternateLabel,
  alternateTo,
  children,
}) {
  return (
    <main className="grid min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] lg:grid-cols-[1.1fr_0.9fr]">
      <section className="flex items-center justify-center px-5 py-10 pb-28 sm:px-10 lg:px-14 lg:py-12">
        <div className="w-full max-w-2xl">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            {eyebrow}
          </p>
          <h1 className="mt-2 text-3xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-4xl">
            {title}
          </h1>
          <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
            {description}
          </p>

          <div className="mt-7">{children}</div>

          <p className="mt-6 text-sm text-[var(--color-graph)]">
            {alternateText}{" "}
            <Link
              to={alternateTo}
              className="font-bold text-[var(--color-bark-dark)] hover:text-[var(--color-sun)]"
            >
              {alternateLabel}
            </Link>
          </p>
        </div>
      </section>

      <aside
        className="relative hidden min-h-[calc(100svh-5rem)] overflow-hidden bg-[var(--color-bark-dark)] text-[var(--color-white)] lg:flex lg:items-center lg:px-12 xl:px-20"
        style={{
          backgroundImage:
            "repeating-linear-gradient(135deg, rgba(255,255,255,0.035) 0px, rgba(255,255,255,0.035) 1px, transparent 1px, transparent 16px)",
        }}
      >
        <div className="relative z-10 max-w-lg">
          <div className="mb-6 flex size-12 items-center justify-center rounded bg-[var(--color-sun)] text-[var(--color-bark-dark)]">
            <Sun aria-hidden="true" size={25} />
          </div>
          <p className="text-xs font-bold uppercase text-[var(--color-sand)]">
            A little time away goes a long way
          </p>
          <h2 className="mt-4 text-4xl font-bold leading-tight xl:text-5xl">
            Make room for a better kind of getaway.
          </h2>
          <p className="mt-5 max-w-sm text-base leading-7 text-white/75">
            Find a stay that fits your plans, whether it is a few hours or a few
            days.
          </p>
          <Link
            to="/"
            className="mt-8 inline-flex items-center gap-2 text-sm font-semibold text-[var(--color-sand)] hover:text-white"
          >
            Explore staycations
            <ArrowUpRight aria-hidden="true" />
          </Link>
        </div>
        <div
          aria-hidden="true"
          className="absolute bottom-10 right-10 flex size-36 items-center justify-center rounded-full border border-white/15 text-white/10 xl:bottom-16 xl:right-16 xl:size-48"
        >
          <Sun size={100} strokeWidth={0.7} />
        </div>
      </aside>
    </main>
  );
}
