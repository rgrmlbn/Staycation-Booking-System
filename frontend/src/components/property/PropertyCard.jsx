import {
  FaArrowRight,
  FaBed,
  FaMapMarkerAlt,
  FaStar,
  FaUsers,
} from "react-icons/fa";
import { Link } from "react-router-dom";

const FALLBACK_IMAGE =
  "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80";

function formatStatus(status) {
  return status?.replaceAll("_", " ").toLowerCase() || "Status unavailable";
}

export default function PropertyCard({
  id,
  title,
  bedrooms,
  maxGuests,
  address,
  status,
  imageUrls,
  reviewScore,
  reviewCount,
}) {
  const imageUrl = imageUrls?.[0] || FALLBACK_IMAGE;
  const formattedStatus = formatStatus(status);
  const isAvailable = formattedStatus === "available";

  return (
    <article className="flex h-full flex-col overflow-hidden rounded border border-[var(--color-mocha)] bg-[var(--color-white)] shadow-[var(--shadow-sm)]">
      <img
        src={imageUrl}
        alt={`${title} staycation home`}
        className="h-48 w-full object-cover"
      />

      <div className="flex flex-1 flex-col p-6">
        <div className="flex min-h-14 items-start justify-between gap-3">
          <h2 className="line-clamp-2 text-xl font-bold text-[var(--color-bark-dark)]">
            {title}
          </h2>
          <span
            className={`inline-flex shrink-0 items-center gap-2 rounded-full px-3 py-1 text-xs font-semibold capitalize ${
              isAvailable
                ? "bg-green-100 text-green-800"
                : "bg-[var(--color-taste)] text-[var(--color-bark)]"
            }`}
          >
            {isAvailable && (
              <span
                aria-hidden="true"
                className="size-2 rounded-full bg-green-600"
              />
            )}
            {formattedStatus}
          </span>
        </div>

        <dl className="mt-5 space-y-3 text-sm text-[var(--color-graph)]">
          <div className="flex items-center gap-3">
            <FaBed
              aria-hidden="true"
              className="shrink-0 text-[var(--color-sun)]"
            />
            <div>
              <dt className="sr-only">Bedrooms</dt>
              <dd>{bedrooms ?? "—"} bedrooms</dd>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <FaUsers
              aria-hidden="true"
              className="shrink-0 text-[var(--color-sun)]"
            />
            <div>
              <dt className="sr-only">Max guests</dt>
              <dd>Up to {maxGuests ?? "—"} guests</dd>
            </div>
          </div>
          <div className="flex items-start gap-3">
            <FaMapMarkerAlt
              aria-hidden="true"
              className="mt-0.5 shrink-0 text-[var(--color-sun)]"
            />
            <div>
              <dt className="sr-only">Address</dt>
              <dd className="line-clamp-2 min-h-10">
                {address || "Address unavailable"}
              </dd>
            </div>
          </div>
        </dl>

        <div className="mt-1 mb-3 flex min-h-7 items-center gap-1.5 text-base">
          <FaStar
            aria-hidden="true"
            className="shrink-0 text-lg text-[var(--color-sand)]"
          />
          <span className="font-bold leading-none text-[var(--color-bark-dark)]">
            {reviewScore == null ? "New" : reviewScore.toFixed(1)}
          </span>
          <span className="leading-none text-[var(--color-graph)]">
            {reviewCount > 0
              ? `(${reviewCount} ${reviewCount === 1 ? "review" : "reviews"})`
              : "No reviews yet"}
          </span>
        </div>

        <Link
          to={`/properties/${id}`}
          className="mt-auto inline-flex min-h-11 items-center justify-center gap-2 rounded bg-[var(--color-bark)] px-5 py-3 text-sm font-bold text-[var(--color-white)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-bark-dark)]"
        >
          View property
          <FaArrowRight aria-hidden="true" className="-rotate-45" size={18} />
        </Link>
      </div>
    </article>
  );
}
