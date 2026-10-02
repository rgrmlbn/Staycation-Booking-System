import { Link } from "react-router-dom";
import { FaCompass, FaHeart, FaHome } from "react-icons/fa";
import { TbArrowBigRightLineFilled } from "react-icons/tb";

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

export default function AboutSection() {
  return (
    <section
      id="about"
      className="scroll-mt-24 border-t border-[var(--color-bark)]/10 bg-[var(--color-cream)] py-16 pb-28 md:py-20 md:pb-20"
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

        <div className="mt-14 flex flex-wrap gap-4 border-t border-[var(--color-bark)]/15 pt-8">
          <Link
            to="/#properties"
            className="inline-flex min-h-12 items-center rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
          >
            Explore properties
          </Link>
          <Link
            to="/host"
            className="inline-flex min-h-12 items-center gap-2 rounded border border-[var(--color-bark)]/30 bg-[var(--color-bark)] px-5 py-3 text-sm font-bold text-[var(--color-white)]"
          >
            Become a host
            <TbArrowBigRightLineFilled aria-hidden="true" />
          </Link>
        </div>
      </div>
    </section>
  );
}
