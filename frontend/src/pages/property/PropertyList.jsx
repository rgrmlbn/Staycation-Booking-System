import PropertyGrid from "../../components/PropertyGrid";

export default function PropertyList() {
  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-16 pb-28 md:py-24 md:pb-24">
      <section id="properties" className="container scroll-mt-24">
        <div className="max-w-2xl">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[var(--color-sun-dark)]">
            Find your next pause
          </p>
          <h1 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
            Places made for a little time away.
          </h1>
          <p className="mt-4 text-base leading-7 text-[var(--color-graph)]">
            Browse stays for slow mornings, easy weekends, and everything in
            between.
          </p>
        </div>

        <div className="mt-12">
          <PropertyGrid pageSize={4} paginated />
        </div>
      </section>
    </main>
  );
}
