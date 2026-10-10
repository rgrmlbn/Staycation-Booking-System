import { useState } from "react";
import {
  FaCalendarAlt,
  FaCheckCircle,
  FaClock,
  FaTimesCircle,
  FaUsers,
} from "react-icons/fa";
import { Link } from "react-router-dom";
import { useBookings } from "../../hooks/useBookings";

function formatPrice(price) {
  if (price == null) return "Price unavailable";
  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
  }).format(price);
}

function formatDateTime(value) {
  if (!value) return "Date unavailable";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Date unavailable";
  return new Intl.DateTimeFormat("en-PH", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(date);
}

function getErrorMessage(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.detail ||
    error?.message ||
    "Something went wrong. Please try again."
  );
}

function BookingStatus({ status }) {
  const normalizedStatus = status?.toUpperCase();
  const presentation = {
    PENDING: {
      label: "Pending",
      className: "border-blue-200 bg-blue-100 text-blue-800",
      Icon: FaClock,
    },
    CONFIRMED: {
      label: "Confirmed",
      className: "border-green-200 bg-green-50 text-green-800",
      Icon: FaCheckCircle,
    },
    REJECTED: {
      label: "Rejected",
      className: "border-red-200 bg-red-50 text-red-800",
      Icon: FaTimesCircle,
    },
    CANCELLED: {
      label: "Cancelled",
      className: "border-red-200 bg-red-100 text-red-800",
      Icon: FaTimesCircle,
    },
    COMPLETED: {
      label: "Completed",
      className: "border-green-200 bg-green-100 text-green-800",
      Icon: FaCheckCircle,
    },
  }[normalizedStatus] ?? {
    label: status?.replaceAll("_", " ").toLowerCase() || "Status unavailable",
    className:
      "border-[var(--color-mocha)] bg-[var(--color-cream)] text-[var(--color-graph)]",
    Icon: FaClock,
  };
  const StatusIcon = presentation.Icon;

  return (
    <span
      className={`inline-flex items-center gap-2 rounded-full border px-3 py-1.5 text-xs font-bold ${presentation.className}`}
    >
      <StatusIcon aria-hidden="true" />
      {presentation.label}
    </span>
  );
}

export default function HostBookings() {
  const [page, setPage] = useState(0);
  const { data, error, isPending } = useBookings({
    role: "host",
    page,
    size: 10,
  });
  const bookings = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? bookings.length;

  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-8 pb-28 md:py-14 md:pb-20">
      <section className="container max-w-6xl">
        <header className="relative overflow-hidden rounded-2xl bg-[var(--color-bark-dark)] px-6 py-8 text-[var(--color-white)] shadow-[var(--shadow-md)] sm:px-10 sm:py-10">
          <div
            aria-hidden="true"
            className="absolute -right-8 -top-14 size-56 rounded-full border-[28px] border-[var(--color-sand)]/10"
          />
          <div className="relative flex flex-col gap-6 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-xs font-bold uppercase tracking-[0.18em] text-[var(--color-sand)]">
                Host account
              </p>
              <h1 className="mt-3 text-4xl font-bold leading-tight sm:text-5xl">
                Property bookings
              </h1>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-white/75 sm:text-base">
                See reservations guests have made for your properties.
              </p>
            </div>
            <Link
              to="/host/dashboard#my-properties"
              className="inline-flex min-h-11 shrink-0 items-center justify-center rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-sun)]"
            >
              View my properties
            </Link>
          </div>
        </header>

        {isPending ? (
          <div
            className="mt-8 rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] p-6 text-[var(--color-graph)] shadow-[var(--shadow-sm)]"
            role="status"
          >
            Loading your property bookings...
          </div>
        ) : error ? (
          <p
            className="mt-8 rounded-lg border border-red-200 bg-red-50 p-5 text-red-800"
            role="alert"
          >
            Could not load property bookings: {getErrorMessage(error)}
          </p>
        ) : bookings.length === 0 ? (
          <div className="mt-8 rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] px-6 py-10 text-center shadow-[var(--shadow-sm)] sm:px-10">
            <span className="mx-auto flex size-14 items-center justify-center rounded-full bg-[var(--color-taste)] text-[var(--color-sun-dark)]">
              <FaCalendarAlt aria-hidden="true" size={22} />
            </span>
            <h2 className="mt-4 text-xl font-bold text-[var(--color-bark-dark)]">
              No bookings for your properties yet.
            </h2>
            <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-[var(--color-graph)]">
              Guest reservations will appear here when someone books one of
              your properties.
            </p>
            <Link
              to="/host/dashboard#my-properties"
              className="mt-6 inline-flex min-h-11 items-center justify-center rounded bg-[var(--color-bark)] px-5 py-3 text-sm font-bold text-[var(--color-white)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-bark-dark)]"
            >
              Manage my properties
            </Link>
          </div>
        ) : (
          <>
            <div className="mb-4 mt-8 flex flex-wrap items-center justify-between gap-2">
              <h2 className="text-lg font-bold text-[var(--color-bark-dark)]">
                Guest reservations
              </h2>
              <p className="text-sm text-[var(--color-graph)]">
                {totalElements} {totalElements === 1 ? "booking" : "bookings"}
              </p>
            </div>
            <ul className="grid gap-5">
              {bookings.map((booking) => (
                <li
                  key={booking.id}
                  className="overflow-hidden rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] shadow-[var(--shadow-sm)]"
                >
                  <div className="border-b border-[var(--color-mocha)] px-5 py-5 sm:px-7">
                    <div className="flex flex-wrap items-center justify-between gap-3">
                      <div>
                        <p className="text-xs font-bold uppercase tracking-[0.12em] text-[var(--color-graph)]">
                          Booking #{booking.id}
                        </p>
                        <h3 className="mt-2 text-xl font-bold text-[var(--color-bark-dark)] sm:text-2xl">
                          {booking.propertyTitle ||
                            `Property #${booking.propertyId}`}
                        </h3>
                      </div>
                      <BookingStatus status={booking.status} />
                    </div>
                    <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
                      <p className="text-sm text-[var(--color-graph)]">
                        Guest:{" "}
                        <span className="font-semibold text-[var(--color-bark-dark)]">
                          {booking.guestName || `Guest #${booking.guestId}`}
                        </span>
                      </p>
                      <p className="text-lg font-bold text-[var(--color-bark-dark)]">
                        {formatPrice(booking.totalPrice)}
                      </p>
                    </div>
                  </div>
                  <div className="grid gap-5 px-5 py-5 sm:grid-cols-[1fr_auto_1fr] sm:items-center sm:px-7">
                    <div className="flex items-start gap-3">
                      <span className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-[var(--color-taste)] text-[var(--color-sun-dark)]">
                        <FaCalendarAlt aria-hidden="true" />
                      </span>
                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wide text-[var(--color-graph)]">
                          Check-in
                        </p>
                        <p className="mt-1 text-sm font-bold text-[var(--color-bark-dark)]">
                          {formatDateTime(booking.checkInDateTime)}
                        </p>
                      </div>
                    </div>
                    <div
                      aria-hidden="true"
                      className="hidden h-px bg-[var(--color-mocha)] sm:block"
                    />
                    <div className="flex items-start gap-3">
                      <span className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-[var(--color-taste)] text-[var(--color-sun-dark)]">
                        <FaCalendarAlt aria-hidden="true" />
                      </span>
                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wide text-[var(--color-graph)]">
                          Check-out
                        </p>
                        <p className="mt-1 text-sm font-bold text-[var(--color-bark-dark)]">
                          {formatDateTime(booking.checkOutDateTime)}
                        </p>
                      </div>
                    </div>
                  </div>
                  <div className="flex flex-wrap items-center gap-2 border-t border-[var(--color-mocha)] bg-[var(--color-cream)]/60 px-5 py-4 text-sm text-[var(--color-graph)] sm:px-7">
                    <FaUsers
                      aria-hidden="true"
                      className="text-[var(--color-sun-dark)]"
                    />
                    {booking.numberOfGuests ?? "—"}{" "}
                    {booking.numberOfGuests === 1 ? "guest" : "guests"}
                    {booking.statusReason && (
                      <p className="basis-full text-sm text-[var(--color-graph)] sm:basis-auto sm:pl-4">
                        <span className="font-semibold text-[var(--color-bark-dark)]">
                          Note:
                        </span>{" "}
                        {booking.statusReason}
                      </p>
                    )}
                  </div>
                </li>
              ))}
            </ul>
            {totalPages > 1 && (
              <nav
                aria-label="Booking pages"
                className="mt-6 flex items-center justify-between rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] p-3 shadow-[var(--shadow-sm)]"
              >
                <button
                  type="button"
                  disabled={page === 0 || isPending}
                  onClick={() => setPage((currentPage) => currentPage - 1)}
                  className="min-h-10 rounded-lg border border-[var(--color-mocha)] px-4 text-sm font-semibold text-[var(--color-bark-dark)] transition-colors hover:bg-[var(--color-cream)] disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Previous
                </button>
                <span className="text-sm font-semibold text-[var(--color-graph)]">
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  type="button"
                  disabled={page + 1 >= totalPages || isPending}
                  onClick={() => setPage((currentPage) => currentPage + 1)}
                  className="min-h-10 rounded-lg border border-[var(--color-mocha)] px-4 text-sm font-semibold text-[var(--color-bark-dark)] transition-colors hover:bg-[var(--color-cream)] disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Next
                </button>
              </nav>
            )}
          </>
        )}
      </section>
    </main>
  );
}
