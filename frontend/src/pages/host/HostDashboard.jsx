import { Link } from "react-router-dom";

export default function HostDashboard() {
  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-16 pb-28 md:py-24 md:pb-24">
      <section className="container">
        <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
          Host dashboard
        </p>
        <h1 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
          Welcome to your host space.
        </h1>
        <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
          Manage your properties and prepare your space for your next guests.
        </p>
        <div className="mt-8 flex flex-wrap gap-4">
          <Link
            to="/host/properties"
            className="inline-flex min-h-12 items-center rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
          >
            Manage properties
          </Link>
          <Link
            to="/host#properties"
            className="inline-flex min-h-12 items-center rounded border border-[var(--color-bark)]/20 px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)]"
          >
            Host information
          </Link>
        </div>
      </section>
    </main>
  );
}
