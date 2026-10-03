import { FaUserPlus, FaHeart, FaHome, FaUsers } from "react-icons/fa";
import { RiLandscapeFill } from "react-icons/ri";
import { Link } from "react-router-dom";
import ContactSection from "../../components/sections/ContactSection";

export default function HostPage() {
  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)]">
      <section
        id="home"
        className="relative overflow-hidden bg-[var(--color-bark-dark)] text-[var(--color-white)]"
      >
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
                <FaUserPlus aria-hidden="true" size={18} />
              </Link>
            </div>
          </div>

          <aside className="border-t border-white/20 pt-8 md:border-l md:border-t-0 md:pl-10 md:pt-0">
            <div className="flex size-12 items-center justify-center rounded bg-[var(--color-sand)] text-[var(--color-bark-dark)]">
              <FaHome aria-hidden="true" size={30} />
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

      <section
        id="properties"
        className="scroll-mt-24 bg-[var(--color-white)] py-16 pb-28 md:py-24 md:pb-24"
      >
        <div className="container">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            Your hosting journey
          </p>
          <h2 className="mt-3 max-w-2xl text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
            Bring your property to more guests.
          </h2>
          <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
            Keep your hosting details close and get ready to welcome guests to a
            stay that feels personal.
          </p>
          <Link
            to="/host/properties"
            className="mt-8 inline-flex min-h-12 items-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
          >
            View your properties
            <RiLandscapeFill aria-hidden="true" size={18} />
          </Link>
        </div>
      </section>

      <section
        id="about"
        className="scroll-mt-24 border-y border-[var(--color-bark)]/10 bg-[var(--color-cream)] py-16 pb-28 md:py-20 md:pb-20"
      >
        <div className="container">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            About Roomance for hosts
          </p>
          <h2 className="mt-3 max-w-2xl text-3xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-4xl">
            Make it easier for guests to feel at home.
          </h2>
          <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
            Roomance helps hosts share their spaces with people looking for a
            comfortable place to pause, recharge, and spend time away.
          </p>
          <div className="mt-10 grid gap-8 border-t border-[var(--color-bark)]/15 pt-8 md:grid-cols-3">
            <article>
              <FaHome
                aria-hidden="true"
                size={28}
                className="text-2xl text-[var(--color-sun)]"
              />
              <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                Share your space
              </h3>
              <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                Introduce guests to the place you have prepared for them.
              </p>
            </article>
            <article>
              <FaUsers
                aria-hidden="true"
                size={28}
                className="text-2xl text-[var(--color-sun)]"
              />
              <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                Welcome new guests
              </h3>
              <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                Connect with people planning a short break or a longer stay.
              </p>
            </article>
            <article>
              <FaHeart
                aria-hidden="true"  
                size={28}
                className="text-2xl text-[var(--color-sun)]"
              />
              <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                Host with care
              </h3>
              <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                Help every visit feel thoughtful, comfortable, and memorable.
              </p>
            </article>
          </div>
        </div>
      </section>

      <ContactSection />
    </main>
  );
}
