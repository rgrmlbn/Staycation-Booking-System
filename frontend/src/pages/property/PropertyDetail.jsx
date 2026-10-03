import { FaArrowLeft, FaBed, FaMapMarkerAlt, FaStar, FaUsers } from "react-icons/fa";
import { Link, useParams } from "react-router-dom";
import PROPERTIES from "../../data/properties";

const FALLBACK_IMAGE =
  "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80";

function displayStatus(status) {
  return status?.replaceAll("_", " ").toLowerCase() || "Status unavailable";
}

function formatPrice(price) {
  if (price == null) return "Price unavailable";
  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
  }).format(price);
}

export default function PropertyDetail() {
  const { id } = useParams();
  const property = PROPERTIES.find((item) => String(item.id) === id);

  if (!property) {
    return (
      <main className="container min-h-[calc(100svh-5rem)] py-16">
        <p className="text-red-700" role="alert">
          Property details are unavailable.
        </p>
        <Link
          to="/properties"
          className="mt-6 inline-flex items-center gap-2 font-semibold text-[var(--color-bark)]"
        >
          <FaArrowLeft aria-hidden="true" /> Back to properties
        </Link>
      </main>
    );
  }

  const images = property.imageUrls?.length
    ? property.imageUrls
    : [FALLBACK_IMAGE];

  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-10 pb-28 md:py-16">
      <div className="container">
        <Link
          to="/properties"
          className="mb-6 inline-flex items-center gap-2 text-sm font-semibold text-[var(--color-bark)] hover:text-[var(--color-sun-dark)]"
        >
          <FaArrowLeft aria-hidden="true" /> Back to properties
        </Link>

        <div className="overflow-hidden rounded border border-[var(--color-mocha)] bg-[var(--color-white)] shadow-[var(--shadow-sm)]">
          <div className="grid gap-2 sm:grid-cols-2">
            {images.map((imageUrl, index) => (
              <img
                key={`${imageUrl}-${index}`}
                src={imageUrl}
                alt={`${property.title} — photo ${index + 1}`}
                className="h-64 w-full object-cover"
              />
            ))}
          </div>

          <div className="p-6 sm:p-10">
            <div className="flex flex-wrap items-center gap-3">
              <span className="rounded-full bg-[var(--color-taste)] px-3 py-1 text-xs font-semibold capitalize text-[var(--color-bark)]">
                {displayStatus(property.status)}
              </span>
              <span className="text-sm text-[var(--color-graph)]">
                Property #{property.id}
              </span>
            </div>
            <h1 className="mt-4 text-3xl font-bold text-[var(--color-bark-dark)] sm:text-4xl">
              {property.title}
            </h1>
            <p className="mt-3 flex items-center gap-2 text-[var(--color-graph)]">
              <FaMapMarkerAlt
                aria-hidden="true"
                className="shrink-0 text-[var(--color-sun)]"
              />
              {property.address}
            </p>

            <div className="mt-6 flex flex-wrap gap-x-6 gap-y-3 text-sm text-[var(--color-graph)]">
              <span className="inline-flex items-center gap-2">
                <FaBed aria-hidden="true" className="text-[var(--color-sun)]" />
                {property.bedrooms ?? "—"} bedrooms
              </span>
              <span>{property.bathrooms ?? "—"} bathrooms</span>
              <span className="inline-flex items-center gap-2">
                <FaUsers aria-hidden="true" className="text-[var(--color-sun)]" />
                Up to {property.maxGuests ?? "—"} guests
              </span>
              <span className="inline-flex items-center gap-2">
                <FaStar aria-hidden="true" className="text-[var(--color-sand)]" />
                {property.reviewScore == null
                  ? "No rating yet"
                  : `${property.reviewScore.toFixed(1)} / 5`}
                {` (${property.reviewCount ?? 0} reviews)`}
              </span>
            </div>

            <section className="mt-10 border-t border-[var(--color-mocha)] pt-8">
              <h2 className="text-xl font-bold text-[var(--color-bark-dark)]">
                About this stay
              </h2>
              <p className="mt-3 whitespace-pre-line leading-7 text-[var(--color-graph)]">
                {property.description || "No description provided."}
              </p>
              <p className="mt-4 text-sm text-[var(--color-graph)]">
                Hosted by {property.hostName || "Host"} (Host #{property.hostId})
              </p>
            </section>

            <section className="mt-10 border-t border-[var(--color-mocha)] pt-8">
              <h2 className="text-xl font-bold text-[var(--color-bark-dark)]">
                Amenities
              </h2>
              {property.amenities?.length ? (
                <ul className="mt-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {property.amenities.map((amenity) => (
                    <li
                      key={amenity.id}
                      className="rounded border border-[var(--color-mocha)] px-4 py-3 text-sm text-[var(--color-graph)]"
                    >
                      {amenity.name}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="mt-3 text-sm text-[var(--color-graph)]">
                  No amenities listed.
                </p>
              )}
            </section>

            <section className="mt-10 border-t border-[var(--color-mocha)] pt-8">
              <h2 className="text-xl font-bold text-[var(--color-bark-dark)]">
                Check-in options
              </h2>
              {property.checkInSlots?.length ? (
                <ul className="mt-4 grid gap-4 sm:grid-cols-2">
                  {property.checkInSlots.map((slot) => (
                    <li
                      key={slot.id}
                      className="rounded border border-[var(--color-mocha)] p-4"
                    >
                      <p className="font-semibold text-[var(--color-bark-dark)]">
                        Starts at {slot.startTime?.slice(0, 5) || "Time unavailable"}
                      </p>
                      <p className="mt-2 text-sm text-[var(--color-graph)]">
                        {slot.durationHours ?? "—"} hours · {formatPrice(slot.price)}
                      </p>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="mt-3 text-sm text-[var(--color-graph)]">
                  No check-in options listed.
                </p>
              )}
            </section>
          </div>
        </div>
      </div>
    </main>
  );
}
