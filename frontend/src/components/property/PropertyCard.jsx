import { FaMapMarkerAlt, FaStar, FaUsers } from "react-icons/fa";

export default function PropertyCard({
  title,
  maxGuests,
  address,
  reviewScore,
  image,
}) {
  const imageUrl =
    image ??
    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80";

  return (
    <article className="flex h-full flex-col rounded border border-[var(--color-mocha)] bg-[var(--color-white)] p-6 shadow-[var(--shadow-sm)]">
      <div className="mb-6 overflow-hidden rounded">
        <img
          src={imageUrl}
          alt={`${title} staycation home`}
          className="h-32 w-full object-cover"
        />
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