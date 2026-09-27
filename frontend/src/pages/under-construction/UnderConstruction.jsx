import { Construction } from "lucide-react";
import { Link } from "react-router-dom";

export default function UnderConstruction() {
  return (
    <main className="flex min-h-[75vh] items-center justify-center bg-[var(--color-cream)] px-6 pt-16 pb-[calc(5rem+env(safe-area-inset-bottom))] text-center md:py-16">
      <div className="flex max-w-xl flex-col items-center">
        <div className="mb-6 flex size-25 items-center justify-center rounded-full bg-[var(--color-sun)]/20 text-[var(--color-sun-dark)]">
          <Construction aria-hidden="true" size={50} />
        </div>
        <p className="mb-3 text-m font-semibold uppercase text-[var(--color-sun-dark)]">
          SORRY FOR THE INCONVENIENCE
        </p>
        <h1 className="text-3xl font-bold text-[var(--color-ink-dark)] sm:text-4xl">
          This page is under construction
        </h1>
        <p className="mt-4 max-w-md text-[15px] text-[var(--color-graph)]">
          We’re putting the finishing touches on this part of your staycation
          experience. Head home to keep exploring.
        </p>
        <Link
          to="/"
          className="mt-8 inline-flex cursor-pointer items-center justify-center rounded bg-[var(--color-sun)] px-5 py-3 text-[15px] font-semibold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
        >
          Back to Home
        </Link>
      </div>
    </main>
  );
}
