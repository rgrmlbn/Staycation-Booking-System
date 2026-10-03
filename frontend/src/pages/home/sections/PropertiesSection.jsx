import { FaArrowRight } from "react-icons/fa";
import { Link } from "react-router-dom";
import PropertyGrid from "../../../components/PropertyGrid";

export default function PropertiesSection() {
  return (
    <section
      id="properties"
      className="scroll-mt-24 bg-[var(--color-white)] py-16 pb-28 md:py-24 md:pb-24"
    >
      <div className="container">
        <div className="max-w-2xl">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            Find your next pause
          </p>
          <h2 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
            Places made for a little time away.
          </h2>
          <p className="mt-4 text-lg leading-8 text-[var(--color-graph)]">
            Browse stays for slow mornings, easy weekends, and everything in
            between.
          </p>
        </div>

        <div className="mt-12">
          <PropertyGrid pageSize={4} />
        </div>
        <Link
          to="/properties"
          className="mt-8 inline-flex min-h-12 items-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-ink-bark)] shadow-[var(--shadow-sm)]"
        >
          Browse all properties
          <FaArrowRight aria-hidden="true" className="-rotate-45" />
        </Link>
      </div>
    </section>
  );
}
