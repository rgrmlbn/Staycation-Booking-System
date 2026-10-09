import { useState } from "react";
import { useForm } from "react-hook-form";
import {
  FaBed,
  FaCompass,
  FaHeart,
  FaHome,
  FaMapMarkerAlt,
  FaSearch,
  FaUsers,
} from "react-icons/fa";
import { PiBrowsersFill } from "react-icons/pi";
import { Link } from "react-router-dom";
import PropertyGrid from "../../components/property/PropertyGrid";
import ContactSection from "../../components/sections/ContactSection";
import {
  GUEST_COUNT_VALIDATION,
  ROOM_COUNT_VALIDATION,
} from "../../utils/validation";

const VALUES = [
  {
    title: "Find your kind of getaway",
    description:
      "Browse staycation spaces and choose a place that fits the time you have and the break you need.",
    icon: FaCompass,
  },
  {
    title: "Feel at home, somewhere new",
    description:
      "We believe even a short change of scenery can make room for rest, connection, and a fresh start.",
    icon: FaHome,
  },
  {
    title: "Make hosting more welcoming",
    description:
      "Roomance gives hosts a place to share their properties with people looking for their next stay.",
    icon: FaHeart,
  },
];

export default function GuestPage() {
  const [searchParams, setSearchParams] = useState(null);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: { place: "", guests: 1, rooms: 1 },
    mode: "onChange",
    shouldFocusError: false,
  });

  return (
    <main className="min-h-screen bg-[var(--color-cream)]">
      <section
        id="home"
        className="relative overflow-hidden bg-[var(--color-bark-dark)] pt-20 pb-28 text-[var(--color-white)] md:pt-28 md:pb-36"
      >
        <div
          aria-hidden="true"
          className="pointer-events-none absolute inset-0 opacity-40 [background-image:repeating-linear-gradient(135deg,rgba(255,255,255,0.06)_0px,rgba(255,255,255,0.06)_1px,transparent_1px,transparent_18px)]"
        />

        <div className="container relative z-10 flex flex-col items-center text-center">
          <h1 className="max-w-3xl text-4xl font-bold leading-[1.08] tracking-tight text-[var(--color-white)] sm:text-5xl md:text-6xl">
            Experience the world&rsquo;s most beautiful{" "}
            <span className="text-[var(--color-sand)]">staycation</span>
          </h1>
          <h2 className="mt-5 max-w-md text-base font-semibold text-white/75 md:text-lg">
            For 24h, 48h, or even just a few hours.
          </h2>

          <div className="mt-12 w-full max-w-3xl">
            <form
              onSubmit={handleSubmit(({ place, guests, rooms }) =>
                setSearchParams({
                  address: place.trim(),
                  guests,
                  rooms,
                }),
              )}
              className="flex flex-col gap-1 rounded bg-[var(--color-white)] p-2 shadow-[var(--shadow-lg)] md:flex-row md:items-center md:gap-0"
            >
              <label className="flex min-w-0 flex-1 items-center gap-3 rounded px-5 py-3 text-left md:basis-2/5 md:flex-none">
                <FaMapMarkerAlt size={18} className="shrink-0 text-[var(--color-sun)]" />
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
                <FaUsers size={18} className="shrink-0 text-[var(--color-sun)]" />
                <span className="flex w-full flex-col">
                  <span className="text-xs font-semibold text-[var(--color-graph)]">
                    Guests
                  </span>
                  <input
                    type="number"
                    aria-invalid={Boolean(errors.guests)}
                    aria-describedby={errors.guests ? "guests-error" : undefined}
                    {...register("guests", GUEST_COUNT_VALIDATION)}
                    min="1"
                    max="6"
                    step="1"
                    className={`w-full border-b bg-transparent text-[13px] text-[var(--color-graph)] focus:outline-none ${
                      errors.guests
                        ? "border-red-600"
                        : "border-transparent"
                    }`}
                  />
                  {errors.guests && (
                    <span
                      id="guests-error"
                      className="text-left text-xs text-red-700"
                    >
                      {errors.guests.message}
                    </span>
                  )}
                </span>
              </label>

              <div className="hidden h-10 w-px bg-[var(--color-mocha)] md:block" />

              <label className="flex min-w-0 flex-1 items-center gap-3 border-t border-[var(--color-mocha)] px-5 py-3 text-left md:border-t-0">
                <FaBed size={18} className="shrink-0 text-[var(--color-sun)]" />
                <span className="flex w-full flex-col">
                  <span className="text-xs font-semibold text-[var(--color-graph)]">
                    Rooms
                  </span>
                  <input
                    type="number"
                    aria-invalid={Boolean(errors.rooms)}
                    aria-describedby={errors.rooms ? "rooms-error" : undefined}
                    {...register("rooms", ROOM_COUNT_VALIDATION)}
                    min="1"
                    max="4"
                    step="1"
                    className={`w-full border-b bg-transparent text-[13px] text-[var(--color-graph)] focus:outline-none ${
                      errors.rooms
                        ? "border-red-600"
                        : "border-transparent"
                    }`}
                  />
                  {errors.rooms && (
                    <span
                      id="rooms-error"
                      className="text-left text-xs text-red-700"
                    >
                      {errors.rooms.message}
                    </span>
                  )}
                </span>
              </label>

              <button
                type="submit"
                className="flex cursor-pointer items-center justify-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-[15px] font-semibold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
              >
                <span>Search</span>
                <FaSearch aria-hidden="true" size={18} />
              </button>
            </form>
          </div>
        </div>
      </section>

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
            <PropertyGrid
              key={JSON.stringify(searchParams)}
              pageSize={4}
              searchParams={searchParams}
            />
          </div>
          <Link
            to="/properties"
            className="mt-8 inline-flex min-h-12 items-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-ink-bark)] shadow-[var(--shadow-sm)]"
          >
            Browse all properties
            <PiBrowsersFill aria-hidden="true" size={18} />
          </Link>
        </div>
      </section>

      <section
        id="about"
        className="scroll-mt-24 border-y border-[var(--color-bark)]/10 bg-[var(--color-cream)] py-16 pb-28 md:py-20 md:pb-20"
      >
        <div className="container">
          <div className="max-w-2xl">
            <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
              What matters to us
            </p>
            <h2 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
              A good stay starts with feeling welcome.
            </h2>
            <p className="mt-4 text-lg leading-8 text-[var(--color-graph)]">
              Whether you are planning a quick change of scenery or welcoming
              guests into your property, Roomance is here to make the search and
              discovery feel more straightforward.
            </p>
          </div>

          <div className="mt-12 grid gap-10 border-t border-[var(--color-bark)]/15 pt-8 md:grid-cols-3 md:gap-8">
            {VALUES.map((value) => {
              const Icon = value.icon;
              return (
                <article key={value.title}>
                  <Icon
                    size={28}
                    aria-hidden="true"
                    className="text-2xl text-[var(--color-sun)]"
                  />
                  <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                    {value.title}
                  </h3>
                  <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                    {value.description}
                  </p>
                </article>
              );
            })}
          </div>
        </div>
      </section>

      <ContactSection />
    </main>
  );
}
