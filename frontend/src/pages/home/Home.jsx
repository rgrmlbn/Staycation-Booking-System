import { MapPin, Users, BedDouble, Search } from "lucide-react";
import { useForm } from "react-hook-form";

export default function Home() {
  const { register, handleSubmit } = useForm({
    defaultValues: {
      place: "",
      guests: 1,
      rooms: 1,
    },
  });

  return (
    <div className="min-h-screen bg-[var(--color-cream)]">
      <section className="relative overflow-hidden pt-20 pb-28 md:pt-28 md:pb-36">
        {/* Decorative blend — the one bold element on the page */}
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -right-40 -top-40 h-[560px] w-[560px] rounded-full opacity-60 blur-3xl md:-right-20 md:-top-52 md:h-[720px] md:w-[720px]"
        />

        <div className="container relative z-10 flex flex-col items-center text-center">
          <h1 className="max-w-3xl text-4xl font-bold leading-[1.08] tracking-tight text-[var(--color-bark-dark)] sm:text-5xl md:text-6xl">
            Experience the world&rsquo;s most beautiful{" "}
            <span className="text-[var(--color-sand)]">staycation</span>
          </h1>
          <h4 className="mt-5 max-w-md text-base font-semibold text-[var(--color-muted)] md:text-lg">
            For 24h, 48h, or even just a few hours.
          </h4>

          {/* Search bar */}
          <div className="mt-12 w-full max-w-3xl">
            <form
              onSubmit={handleSubmit(() => {})}
              className="flex flex-col gap-1 rounded bg-[var(--color-white)] p-2 shadow-[var(--shadow-lg)] md:flex-row md:items-center md:gap-0 md:rounded"
            >
              {/* Place */}
              <label className="flex min-w-0 flex-1 items-center gap-3 rounded px-5 py-3 text-left md:basis-2/5 md:flex-none md:rounded-full">
                <MapPin className="shrink-0 text-[var(--color-sun)]" />
                <span className="flex w-full flex-col">
                  <span className="text-xs font-semibold text-[var(--color-graph)]">
                    Place
                  </span>
                  <input
                    type="text"
                    {...register("place")}
                    placeholder="Where are you staying?"
                    className="w-full bg-transparent text-[13px] text-[var(--color-graph)] placeholder:text-[var(--color-graph)]/70 focus:outline-none"
                  />
                </span>
              </label>

              <div className="hidden h-10 w-px bg-[var(--color-mocha)] md:block" />

              {/* Guest */}
              <label className="flex min-w-0 flex-1 items-center gap-3 border-t border-[var(--color-mocha)] px-5 py-3 text-left md:border-t-0">
                <Users className="shrink-0 text-[var(--color-sun)]" />
                <span className="flex w-full flex-col">
                  <span className="text-xs font-semibold text-[var(--color-graph)]">
                    Guests
                  </span>
                  <input
                    type="number"
                    {...register("guests", { valueAsNumber: true })}
                    min="1"
                    max="6"
                    step="1"
                    className="w-full bg-transparent text-[13px] text-[var(--color-graph)] focus:outline-none"
                  />
                </span>
              </label>

              <div className="hidden h-10 w-px bg-[var(--color-mocha)] md:block" />

              {/* Room */}
              <label className="flex min-w-0 flex-1 items-center gap-3 border-t border-[var(--color-mocha)] px-5 py-3 text-left md:border-t-0">
                <BedDouble className="shrink-0 text-[var(--color-sun)]" />
                <span className="flex w-full flex-col">
                  <span className="text-xs font-semibold text-[var(--color-graph)]">
                    Rooms
                  </span>
                  <input
                    type="number"
                    {...register("rooms", { valueAsNumber: true })}
                    min="1"
                    max="4"
                    step="1"
                    className="w-full bg-transparent text-[13px] text-[var(--color-graph)] focus:outline-none"
                  />
                </span>
              </label>

              {/* Search button */}
              <button
                type="submit"
                className="flex cursor-pointer items-center justify-center gap-2 rounded bg-[var(--color-sun)] px-5 py-3 text-[15px] font-semibold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
              >
                <span>Search</span>
                <Search />
              </button>
            </form>
          </div>
        </div>
      </section>
    </div>
  );
}
