import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { useCreateBooking } from "../../hooks/useBookings";
import { useProperty } from "../../hooks/useProperties";

function formatPrice(price) {
  if (price == null) return "Price unavailable";
  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
  }).format(price);
}

function getErrorMessage(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.detail ||
    error?.message ||
    "Something went wrong. Please try again."
  );
}

function getTomorrowDate() {
  const date = new Date();
  date.setDate(date.getDate() + 1);
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0"),
  ].join("-");
}

export default function GuestBookings() {
  const [searchParams] = useSearchParams();
  const propertyId = searchParams.get("propertyId");
  const {
    data: property,
    error: propertyError,
    isPending: isLoadingProperty,
  } = useProperty(propertyId);
  const createBooking = useCreateBooking();
  const [checkInDate, setCheckInDate] = useState(getTomorrowDate);
  const [numberOfGuests, setNumberOfGuests] = useState("1");
  const checkInSlots = property?.checkInSlots ?? [];
  const selectedSlotId = checkInSlots[0]?.id;

  const handleSubmit = (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    createBooking.mutate({
      propertyId: Number(propertyId),
      checkInSlotId: Number(formData.get("checkInSlotId")),
      checkInDate,
      numberOfGuests: Number(numberOfGuests),
    });
  };

  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-16 pb-28 md:py-24 md:pb-24">
      <section className="container">
        <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
          Book your stay
        </p>
        <h1 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
          Request a reservation.
        </h1>
        <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
          Choose a check-in option and tell the host when you would like to
          stay.
        </p>
        {!propertyId ? (
          <p className="mt-6 text-red-700" role="alert">
            Select a property before requesting a booking.
          </p>
        ) : isLoadingProperty ? (
          <p className="mt-6 text-sm text-[var(--color-graph)]" role="status">
            Loading property details...
          </p>
        ) : propertyError || !property ? (
          <p className="mt-6 text-red-700" role="alert">
            Could not load this property:{" "}
            {getErrorMessage(propertyError)}
          </p>
        ) : (
          <div className="mt-8 grid max-w-4xl gap-8 lg:grid-cols-2">
            <article className="overflow-hidden rounded border border-[var(--color-mocha)] bg-[var(--color-white)] shadow-[var(--shadow-sm)]">
              {property.imageUrls?.[0] && (
                <img
                  src={property.imageUrls[0]}
                  alt={`${property.title} staycation home`}
                  className="h-56 w-full object-cover"
                />
              )}
              <div className="p-5">
                <h2 className="text-xl font-bold text-[var(--color-bark-dark)]">
                  {property.title}
                </h2>
                <p className="mt-2 text-sm text-[var(--color-graph)]">
                  {property.address}
                </p>
                <p className="mt-4 text-sm text-[var(--color-graph)]">
                  Hosted by {property.hostName || "Host"}
                </p>
                <Link
                  to={`/properties/${property.id}`}
                  className="mt-4 inline-flex font-semibold text-[var(--color-bark)] underline"
                >
                  View property details
                </Link>
              </div>
            </article>

            <form
              onSubmit={handleSubmit}
              className="grid content-start gap-5 rounded border border-[var(--color-mocha)] bg-[var(--color-white)] p-5 shadow-[var(--shadow-sm)]"
            >
              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Check-in option
                <select
                  name="checkInSlotId"
                  required
                  disabled={checkInSlots.length === 0 || createBooking.isPending}
                  defaultValue={selectedSlotId ?? ""}
                  className="min-h-12 rounded border border-[var(--color-mocha)] bg-white px-3 font-normal"
                >
                  {checkInSlots.length === 0 ? (
                    <option value="">No check-in options available</option>
                  ) : (
                    checkInSlots.map((slot) => (
                      <option key={slot.id} value={slot.id}>
                        {slot.startTime?.slice(0, 5) || "Time unavailable"} ·{" "}
                        {slot.durationHours ?? "—"} hours · {formatPrice(slot.price)}
                      </option>
                    ))
                  )}
                </select>
              </label>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Check-in date
                <input
                  type="date"
                  required
                  min={getTomorrowDate()}
                  value={checkInDate}
                  onChange={(event) => setCheckInDate(event.target.value)}
                  disabled={createBooking.isPending}
                  className="min-h-12 rounded border border-[var(--color-mocha)] px-3 font-normal"
                />
              </label>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Number of guests
                <input
                  type="number"
                  required
                  min="1"
                  max={property.maxGuests ?? undefined}
                  value={numberOfGuests}
                  onChange={(event) => setNumberOfGuests(event.target.value)}
                  disabled={createBooking.isPending}
                  className="min-h-12 rounded border border-[var(--color-mocha)] px-3 font-normal"
                />
              </label>

              <button
                type="submit"
                disabled={
                  checkInSlots.length === 0 ||
                  createBooking.isPending ||
                  Number(numberOfGuests) > property.maxGuests
                }
                className="inline-flex min-h-12 items-center justify-center rounded bg-[var(--color-bark)] px-5 py-3 text-sm font-bold text-[var(--color-white)] shadow-[var(--shadow-sm)] disabled:cursor-not-allowed disabled:opacity-50"
              >
                {createBooking.isPending ? "Submitting request..." : "Request booking"}
              </button>

              {createBooking.error && (
                <p className="text-sm text-red-700" role="alert">
                  Could not submit booking: {getErrorMessage(createBooking.error)}
                </p>
              )}
              {createBooking.isSuccess && (
                <p className="text-sm text-green-800" role="status">
                  Booking request submitted successfully.
                </p>
              )}
            </form>
          </div>
        )}
      </section>
    </main>
  );
}
