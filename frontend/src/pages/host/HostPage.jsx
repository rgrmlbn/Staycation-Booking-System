import {
  ArrowRight,
  HousePlus,
} from "lucide-react";
import { Link } from "react-router-dom";


export default function HostPage() {
  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)]">
      <section className="relative overflow-hidden bg-[var(--color-bark-dark)] text-[var(--color-white)]">
        <div
          aria-hidden="true"
          className="pointer-events-none absolute inset-0 opacity-40 [background-image:repeating-linear-gradient(135deg,rgba(255,255,255,0.06)_0px,rgba(255,255,255,0.06)_1px,transparent_1px,transparent_18px)]"
        />
        <div className="container relative grid gap-12 py-16 md:grid-cols-[1.2fr_0.8fr] md:items-center md:py-24">
          <div className="max-w-2xl">
            <p className="text-xs font-bold uppercase text-[var(--color-sand)]">
              Roomance for hosts
            </p>
            <h1 className="mt-4 text-4xl font-bold leading-tight sm:text-5xl">
              Bring your space to Roomance.
            </h1>
            <p className="mt-5 max-w-xl text-base leading-7 text-white/75">
              Share the place you love and help guests find a stay that feels
              like their own.
            </p>
            <div className="mt-8 flex flex-wrap items-center gap-4">
              <Link
                to="/host/register"
                className="inline-flex min-h-12 items-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
              >
                Create a host account
                <ArrowRight aria-hidden="true" size={18} />
              </Link>
            </div>
          </div>

          <aside className="border-t border-white/20 pt-8 md:border-l md:border-t-0 md:pl-10 md:pt-0">
            <div className="flex size-12 items-center justify-center rounded bg-[var(--color-sun)] text-[var(--color-bark-dark)]">
              <HousePlus aria-hidden="true" size={25} />
            </div>
            <h2 className="mt-5 text-2xl font-bold">
              A good stay starts at home.
            </h2>
            <p className="mt-3 text-sm leading-6 text-white/75">
              Create a host account to get started.
            </p>
          </aside>
        </div>
      </section>

    </main>
  );
}
