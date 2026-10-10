import { useState } from "react";
import {
  FaArrowRight,
  FaCalendarAlt,
  FaCheckCircle,
  FaClock,
  FaTimesCircle,
  FaUsers,
} from "react-icons/fa";
import { Link, useSearchParams } from "react-router-dom";
import {
  useBookings,
  useCancelBooking,
  useCreateBooking,
  useUpdateBooking,
} from "../../hooks/useBookings";
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

function formatDateTime(value) {
  if (!value) return "Date unavailable";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Date unavailable";
  return new Intl.DateTimeFormat("en-PH", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(date);
}

function getStatusPresentation(status) {
  const normalizedStatus = status?.toUpperCase();
  const statusPresentation = {
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
  };

  return (
    statusPresentation[normalizedStatus] || {
      label: status?.replaceAll("_", " ").toLowerCase() || "Status unavailable",
      className:
        "border-[var(--color-mocha)] bg-[var(--color-cream)] text-[var(--color-graph)]",
      Icon: FaClock,
    }
  );
}

function BookingStatus({ status }) {
  const presentation = getStatusPresentation(status);
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

function BookingEditForm({ booking, onClose }) {
  const { data: property, error: propertyError, isPending: isLoadingProperty } =
    useProperty(booking.propertyId);
  const updateBooking = useUpdateBooking();
  const [checkInDate, setCheckInDate] = useState(
    booking.checkInDateTime?.slice(0, 10) ?? getTomorrowDate(),
  );
  const [checkInSlotId, setCheckInSlotId] = useState(
    String(booking.checkInSlotId ?? ""),
  );
  const [numberOfGuests, setNumberOfGuests] = useState(
    String(booking.numberOfGuests ?? 1),
  );
  const checkInSlots = property?.checkInSlots ?? [];

  const handleSubmit = (event) => {
    event.preventDefault();
    updateBooking.mutate(
      {
        id: booking.id,
        checkInSlotId: Number(checkInSlotId),
        checkInDate,
        numberOfGuests: Number(numberOfGuests),
      },
      { onSuccess: onClose },
    );
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="grid gap-4 border-t border-[var(--color-mocha)] bg-[var(--color-cream)]/60 px-5 py-5 sm:px-7"
    >
      <h4 className="text-sm font-bold text-[var(--color-bark-dark)]">
        Edit booking details
      </h4>
      {isLoadingProperty ? (
        <p className="text-sm text-[var(--color-graph)]" role="status">
          Loading property options...
        </p>
      ) : propertyError || !property ? (
        <p className="text-sm text-red-700" role="alert">
          Could not load check-in options: {getErrorMessage(propertyError)}
        </p>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-3">
            <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
              Check-in option
              <select
                required
                value={checkInSlotId}
                onChange={(event) => setCheckInSlotId(event.target.value)}
                disabled={checkInSlots.length === 0 || updateBooking.isPending}
                className="min-h-11 rounded border border-[var(--color-mocha)] bg-white px-3 font-normal"
              >
                {checkInSlots.length === 0 ? (
                  <option value="">No check-in options available</option>
                ) : (
                  checkInSlots.map((slot) => (
                    <option key={slot.id} value={slot.id}>
                      {slot.startTime?.slice(0, 5) || "Time unavailable"} ·{" "}
                      {slot.durationHours ?? "—"} hours ·{" "}
                      {formatPrice(slot.price)}
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
                disabled={updateBooking.isPending}
                className="min-h-11 rounded border border-[var(--color-mocha)] px-3 font-normal"
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
                disabled={updateBooking.isPending}
                className="min-h-11 rounded border border-[var(--color-mocha)] px-3 font-normal"
              />
            </label>
          </div>
          {updateBooking.error && (
            <p className="text-sm text-red-700" role="alert">
              Could not update booking: {getErrorMessage(updateBooking.error)}
            </p>
          )}
          <div className="flex flex-wrap justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              disabled={updateBooking.isPending}
              className="min-h-10 rounded border border-[var(--color-mocha)] bg-[var(--color-white)] px-4 text-sm font-semibold text-[var(--color-bark-dark)] disabled:opacity-50"
            >
              Keep booking
            </button>
            <button
              type="submit"
              disabled={
                checkInSlots.length === 0 ||
                updateBooking.isPending ||
                Number(numberOfGuests) > property.maxGuests
              }
              className="min-h-10 rounded bg-[var(--color-bark)] px-4 text-sm font-bold text-white disabled:cursor-not-allowed disabled:opacity-50"
            >
              {updateBooking.isPending ? "Saving..." : "Save changes"}
            </button>
          </div>
        </>
      )}
    </form>
  );
}

function BookingList() {
  const [page, setPage] = useState(0);
  const [editingBookingId, setEditingBookingId] = useState(null);
  const [cancellingBookingId, setCancellingBookingId] = useState(null);
  const [cancelReason, setCancelReason] = useState("");
  const [cancelValidationError, setCancelValidationError] = useState("");
  const [cancelErrorBookingId, setCancelErrorBookingId] = useState(null);
  const { data, error, isPending } = useBookings({ page, size: 10 });
  const cancelBooking = useCancelBooking();
  const bookings = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  const openCancellationForm = (bookingId) => {
    setEditingBookingId(null);
    setCancellingBookingId(bookingId);
    setCancelReason("");
    setCancelValidationError("");
    setCancelErrorBookingId(null);
    cancelBooking.reset();
  };

  const closeEditForm = () => setEditingBookingId(null);

  const closeCancellationForm = () => {
    setCancellingBookingId(null);
    setCancelReason("");
    setCancelValidationError("");
    setCancelErrorBookingId(null);
    cancelBooking.reset();
  };

  const handleCancelBooking = (event, bookingId) => {
    event.preventDefault();
    const reason = cancelReason.trim();

    if (reason.length < 10) {
      setCancelValidationError("Please provide at least 10 characters.");
      return;
    }

    setCancelValidationError("");
    setCancelErrorBookingId(bookingId);
    cancelBooking.mutate(
      { id: bookingId, reason },
      {
        onSuccess: closeCancellationForm,
      },
    );
  };

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
                Guest account
              </p>
              <h1 className="mt-3 text-4xl font-bold leading-tight sm:text-5xl">
                My bookings
              </h1>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-white/75 sm:text-base">
                Keep track of your stay requests, upcoming check-ins, and
                completed getaways.
              </p>
            </div>
            <Link
              to="/properties"
              className="inline-flex min-h-11 shrink-0 items-center justify-center gap-2 self-start rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-sun)] sm:self-auto"
            >
              Find a stay
              <FaArrowRight aria-hidden="true" />
            </Link>
          </div>
        </header>

        {isPending ? (
          <div
            className="mt-8 rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] p-6 text-[var(--color-graph)] shadow-[var(--shadow-sm)]"
            role="status"
          >
            Loading your bookings...
          </div>
        ) : error ? (
          <p
            className="mt-8 rounded-lg border border-red-200 bg-red-50 p-5 text-red-800"
            role="alert"
          >
            Could not load your bookings: {getErrorMessage(error)}
          </p>
        ) : bookings.length === 0 ? (
          <div className="mt-8 rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] px-6 py-10 text-center shadow-[var(--shadow-sm)] sm:px-10">
            <span className="mx-auto flex size-14 items-center justify-center rounded-full bg-[var(--color-taste)] text-[var(--color-sun-dark)]">
              <FaCalendarAlt aria-hidden="true" size={22} />
            </span>
            <h2 className="mt-4 text-xl font-bold text-[var(--color-bark-dark)]">
              You don&apos;t have any bookings yet.
            </h2>
            <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-[var(--color-graph)]">
              When you request a stay, its details and status will appear here.
            </p>
            <Link
              to="/properties"
              className="mt-6 inline-flex min-h-11 items-center gap-2 rounded bg-[var(--color-bark)] px-5 py-3 text-sm font-bold text-[var(--color-white)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-bark-dark)]"
            >
              Explore properties <FaArrowRight aria-hidden="true" />
            </Link>
          </div>
        ) : (
          <>
            <div className="mb-4 mt-8 flex flex-wrap items-center justify-between gap-2">
              <h2 className="text-lg font-bold text-[var(--color-bark-dark)]">
                Your stays
              </h2>
              <p className="text-sm text-[var(--color-graph)]">
                {data?.totalElements ?? bookings.length}{" "}
                {(data?.totalElements ?? bookings.length) === 1
                  ? "booking"
                  : "bookings"}
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
                      <p className="text-xs font-bold uppercase tracking-[0.12em] text-[var(--color-graph)]">
                        Booking #{booking.id}
                      </p>
                      <BookingStatus status={booking.status} />
                    </div>
                    <div className="mt-3 flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <h3 className="text-xl font-bold text-[var(--color-bark-dark)] sm:text-2xl">
                          {booking.propertyTitle || `Property #${booking.propertyId}`}
                        </h3>
                      </div>
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
                  <div className="flex flex-col gap-4 border-t border-[var(--color-mocha)] bg-[var(--color-cream)]/60 px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-7">
                    <div className="flex items-center gap-2 text-sm text-[var(--color-graph)]">
                      <FaUsers
                        aria-hidden="true"
                        className="text-[var(--color-sun-dark)]"
                      />
                      {booking.numberOfGuests ?? "—"}{" "}
                      {booking.numberOfGuests === 1 ? "guest" : "guests"}
                    </div>
                    {booking.statusReason && (
                      <p className="text-sm text-[var(--color-graph)]">
                        <span className="font-semibold text-[var(--color-bark-dark)]">
                          Note:
                        </span>{" "}
                        {booking.statusReason}
                      </p>
                    )}
                    <div className="flex flex-wrap items-center gap-4">
                      {booking.status?.toUpperCase() === "PENDING" && (
                        <>
                          <button
                            type="button"
                            onClick={() => {
                              setCancellingBookingId(null);
                              setEditingBookingId(
                                editingBookingId === booking.id
                                  ? null
                                  : booking.id,
                              );
                            }}
                            className="text-sm font-semibold text-[var(--color-bark)] underline decoration-[var(--color-mocha)] underline-offset-4 hover:text-[var(--color-sun-dark)]"
                          >
                            {editingBookingId === booking.id
                              ? "Close edit"
                              : "Edit booking"}
                          </button>
                          <button
                            type="button"
                            onClick={() => openCancellationForm(booking.id)}
                            className="text-sm font-semibold text-red-700 underline decoration-red-300 underline-offset-4 hover:text-red-900"
                          >
                            Cancel booking
                          </button>
                        </>
                      )}
                      <Link
                        to={`/properties/${booking.propertyId}`}
                        className="inline-flex items-center gap-2 text-sm font-bold text-[var(--color-bark)] hover:text-[var(--color-sun-dark)]"
                      >
                        View property
                        <FaArrowRight aria-hidden="true" size={12} />
                      </Link>
                    </div>
                  </div>
                  {editingBookingId === booking.id && (
                    <BookingEditForm
                      booking={booking}
                      onClose={closeEditForm}
                    />
                  )}
                  {cancellingBookingId === booking.id && (
                    <form
                      onSubmit={(event) =>
                        handleCancelBooking(event, booking.id)
                      }
                      className="grid gap-3 border-t border-red-100 bg-red-50/70 px-5 py-5 sm:px-7"
                    >
                      <div>
                        <label
                          htmlFor={`cancel-reason-${booking.id}`}
                          className="block text-sm font-bold text-[var(--color-bark-dark)]"
                        >
                          Why are you cancelling?
                        </label>
                        <p
                          id={`cancel-reason-help-${booking.id}`}
                          className="mt-1 text-xs text-[var(--color-graph)]"
                        >
                          Please provide at least 10 characters.
                        </p>
                      </div>
                      <textarea
                        id={`cancel-reason-${booking.id}`}
                        value={cancelReason}
                        onChange={(event) => {
                          setCancelReason(event.target.value);
                          setCancelValidationError("");
                        }}
                        minLength={10}
                        maxLength={500}
                        required
                        rows={3}
                        aria-describedby={`cancel-reason-help-${booking.id}`}
                        aria-invalid={Boolean(cancelValidationError)}
                        className="w-full rounded border border-[var(--color-mocha)] bg-[var(--color-white)] px-3 py-2 text-sm text-[var(--color-bark-dark)] outline-none focus:border-[var(--color-sun-dark)] focus:ring-2 focus:ring-[var(--color-sand)] aria-[invalid=true]:border-red-600"
                        placeholder="Enter your cancellation reason..."
                      />
                      {cancelValidationError && (
                        <p className="text-sm text-red-700" role="alert">
                          {cancelValidationError}
                        </p>
                      )}
                      {cancelErrorBookingId === booking.id &&
                        cancelBooking.error && (
                          <p className="text-sm text-red-700" role="alert">
                            Could not cancel booking:{" "}
                            {getErrorMessage(cancelBooking.error)}
                          </p>
                        )}
                      <div className="flex flex-wrap justify-end gap-3">
                        <button
                          type="button"
                          onClick={closeCancellationForm}
                          disabled={cancelBooking.isPending}
                          className="min-h-10 rounded border border-[var(--color-mocha)] bg-[var(--color-white)] px-4 text-sm font-semibold text-[var(--color-bark-dark)] disabled:opacity-50"
                        >
                          Keep booking
                        </button>
                        <button
                          type="submit"
                          disabled={cancelBooking.isPending}
                          className="min-h-10 rounded bg-red-700 px-4 text-sm font-bold text-white transition-colors hover:bg-red-800 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          {cancelBooking.isPending
                            ? "Cancelling..."
                            : "Confirm cancellation"}
                        </button>
                      </div>
                    </form>
                  )}
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

  if (!propertyId) {
    return <BookingList />;
  }

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
