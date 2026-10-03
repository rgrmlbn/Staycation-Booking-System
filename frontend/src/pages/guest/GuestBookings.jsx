import { Link, useSearchParams } from "react-router-dom";
import { FaSignInAlt } from "react-icons/fa";
import PROPERTIES from "../../data/properties";

function formatPrice(price) {
  if (price == null) return "Price unavailable";
  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
  }).format(price);
}

export default function GuestBookings() {
  const [searchParams] = useSearchParams();
  const propertyId = searchParams.get("propertyId");
  const property = PROPERTIES.find(
    (item) => String(item.id) === propertyId,
  );
  const startingPrice = property?.checkInSlots?.length
    ? Math.min(...property.checkInSlots.map((slot) => slot.price))
    : null;

  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-16 pb-28 md:py-24 md:pb-24">
      <section className="container">
        <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
          Guest bookings
        </p>
        <h1 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
          Your next stay starts here.
        </h1>
        <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
          Sign in to book a stay and keep your travel plans close.
        </p>
        {property && (
          <div className="mt-6 max-w-2xl rounded border border-[var(--color-mocha)] bg-[var(--color-white)] p-5">
            <p className="font-bold text-[var(--color-bark-dark)]">
              {property.title}
            </p>
            <p className="mt-1 text-sm text-[var(--color-graph)]">
              {property.address}
              {startingPrice != null
                ? ` · From ${formatPrice(startingPrice)} per stay`
                : ""}
            </p>
          </div>
        )}
        <Link
          to="/login"
          className="mt-8 inline-flex min-h-12 items-center justify-center gap-2 rounded bg-[var(--color-bark)] px-5 py-3 text-sm font-bold text-[var(--color-white)] shadow-[var(--shadow-sm)]"
        >
          Guest sign in
                    <FaSignInAlt aria-hidden="true" size={18} />
        </Link>
      </section>
    </main>
  );
}
