import { Link } from "react-router-dom";

export default function GuestDashboard() {
  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-16 pb-28 md:py-24 md:pb-24">
      <section className="container">
        <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
          Guest dashboard
        </p>
        <h1 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
          Your next stay starts here.
        </h1>
        <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
          Find a place for your next break and keep your travel plans close.
        </p>
        <Link
          to="/#properties"
          className="mt-8 inline-flex min-h-12 items-center rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
        >
          Explore properties
        </Link>
      </section>
    </main>
  );
}
