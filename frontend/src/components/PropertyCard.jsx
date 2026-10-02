import { FaMapMarkerAlt, FaStar, FaUsers } from "react-icons/fa";

export default function PropertyCard({
  title,
  maxGuests,
  address,
  reviewScore,
}) {
  return (
    <article className="flex h-full flex-col rounded border border-[var(--color-mocha)] bg-[var(--color-white)] p-6 shadow-[var(--shadow-sm)]">
      <div className="mb-6 flex h-32 items-end rounded bg-[var(--color-bark-dark)] p-5 text-[var(--color-white)] [background-image:repeating-linear-gradient(135deg,rgba(255,255,255,0.08)_0px,rgba(255,255,255,0.08)_1px,transparent_1px,transparent_16px)]">
        <span className="text-xs font-bold uppercase tracking-[0.18em] text-[var(--color-sand)]">
          Staycation home
        </span>
      </div>

      <h2 className="text-xl font-bold text-[var(--color-bark-dark)]">
        {title}
      </h2>

      <dl className="mt-5 space-y-3 text-sm text-[var(--color-graph)]">
        <div className="flex items-center gap-3">
          <FaUsers
            aria-hidden="true"
            className="shrink-0 text-[var(--color-sun)]"
          />
          <div>
            <dt className="sr-only">Max guests</dt>
            <dd>Up to {maxGuests} guests</dd>
          </div>
        </div>
        <div className="flex items-start gap-3">
          <FaMapMarkerAlt
            aria-hidden="true"
            className="mt-0.5 shrink-0 text-[var(--color-sun)]"
          />
          <div>
            <dt className="sr-only">Address</dt>
            <dd>{address}</dd>
          </div>
        </div>
      </dl>

      <div className="mt-5 flex items-center gap-1">
        <FaStar aria-hidden="true" className="text-[var(--color-sand)]" />
        <span className="font-bold text-[var(--color-bark-dark)]">
          {reviewScore}
        </span>
        <span className="text-sm text-[var(--color-graph)]">Review score</span>
      </div>
    </article>
  );
}
