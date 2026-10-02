import {
  FaBed,
  FaEnvelope,
  FaMapMarkerAlt,
  FaPhoneAlt,
  FaSearch,
  FaUsers,
} from "react-icons/fa";
import { useForm } from "react-hook-form";
import { PropertiesSection } from "../property/PropertyList";
import { AboutSection } from "../about/About";

export default function GuestPage() {
  const { register, handleSubmit } = useForm({
    defaultValues: {
      place: "",
      guests: 1,
      rooms: 1,
    },
  });

  return (
    <div className="min-h-screen bg-[var(--color-cream)]">
      <section className="relative overflow-hidden bg-[var(--color-bark-dark)] pt-20 pb-28 text-[var(--color-white)] md:pt-28 md:pb-36">
        <div
          aria-hidden="true"
          className="pointer-events-none absolute inset-0 opacity-40 [background-image:repeating-linear-gradient(135deg,rgba(255,255,255,0.06)_0px,rgba(255,255,255,0.06)_1px,transparent_1px,transparent_18px)]"
        />

        <div className="container relative z-10 flex flex-col items-center text-center">
          <h1 className="max-w-3xl text-4xl font-bold leading-[1.08] tracking-tight text-[var(--color-white)] sm:text-5xl md:text-6xl">
            Experience the world&rsquo;s most beautiful{" "}
            <span className="text-[var(--color-sand)]">staycation</span>
          </h1>
          <h4 className="mt-5 max-w-md text-base font-semibold text-white/75 md:text-lg">
            For 24h, 48h, or even just a few hours.
          </h4>

          <div className="mt-12 w-full max-w-3xl">
            <form
              onSubmit={handleSubmit(() => {})}
              className="flex flex-col gap-1 rounded bg-[var(--color-white)] p-2 shadow-[var(--shadow-lg)] md:flex-row md:items-center md:gap-0 md:rounded"
            >
              <label className="flex min-w-0 flex-1 items-center gap-3 rounded px-5 py-3 text-left md:basis-2/5 md:flex-none md:rounded-full">
                <FaMapMarkerAlt className="shrink-0 text-[var(--color-sun)]" />
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

              <label className="flex min-w-0 flex-1 items-center gap-3 border-t border-[var(--color-mocha)] px-5 py-3 text-left md:border-t-0">
                <FaUsers className="shrink-0 text-[var(--color-sun)]" />
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

              <label className="flex min-w-0 flex-1 items-center gap-3 border-t border-[var(--color-mocha)] px-5 py-3 text-left md:border-t-0">
                <FaBed className="shrink-0 text-[var(--color-sun)]" />
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

              <button
                type="submit"
                className="flex cursor-pointer items-center justify-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-[15px] font-semibold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
              >
                <span>Search</span>
                <FaSearch />
              </button>
            </form>
          </div>
        </div>
      </section>
      <PropertiesSection />
      <AboutSection />
      <section
        id="contact"
        className="scroll-mt-24 border-t border-[var(--color-bark)]/10 bg-[var(--color-white)] py-16 pb-28 text-[var(--color-bark-dark)] md:py-20 md:pb-20"
      >
        <div className="container">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            Contact
          </p>
          <h2 className="mt-3 text-3xl font-bold leading-tight sm:text-4xl">
            Let’s help plan your next stay.
          </h2>
          <p className="mt-4 max-w-xl text-sm leading-6 text-[var(--color-graph)]">
            Have a question about Roomance or need a hand with your plans? Get
            in touch with our team.
          </p>
          <div className="mt-7 flex flex-col gap-4 sm:flex-row sm:gap-8">
            <a
              href="tel:+63281234567"
              className="inline-flex items-center gap-3 text-sm font-semibold text-[var(--color-bark-dark)] hover:text-[var(--color-sun-dark)]"
            >
              <FaPhoneAlt
                aria-hidden="true"
                className="text-[var(--color-sun)]"
              />
              +63 (2) 8123-4567
            </a>
            <a
              href="mailto:roomance@gmail.com"
              className="inline-flex items-center gap-3 text-sm font-semibold text-[var(--color-bark-dark)] hover:text-[var(--color-sun-dark)]"
            >
              <FaEnvelope
                aria-hidden="true"
                className="text-[var(--color-sun)]"
              />
              roomance@gmail.com
            </a>
          </div>
        </div>
      </section>
    </div>
  );
}
