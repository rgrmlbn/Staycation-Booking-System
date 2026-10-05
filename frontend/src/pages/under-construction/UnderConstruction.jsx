import { FaArrowRight, FaTools } from "react-icons/fa";
import { Link } from "react-router-dom";

export default function UnderConstruction() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-[var(--color-cream)] px-6 py-16 text-center">
      <div className="flex max-w-xl flex-col items-center">
        <div className="mb-6 flex size-25 items-center justify-center rounded-full bg-[var(--color-sun)]/20 text-[var(--color-sun-dark)]">
          <FaTools aria-hidden="true" size={50} />
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
          className="mt-8 inline-flex cursor-pointer items-center justify-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-[15px] font-semibold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
        >
          Back to Home
          <FaArrowRight aria-hidden="true" size={14} />
        </Link>
      </div>
    </main>
  );
}
