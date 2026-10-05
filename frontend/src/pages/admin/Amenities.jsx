import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import amenityService from "../../services/amenityService";
import { QUERY_KEYS } from "../../utils/constants";

function getErrorMessage(error) {
  return (
    error.response?.data?.message ||
    error.message ||
    "The amenity request failed. Please try again."
  );
}

export default function Amenities() {
  const [name, setName] = useState("");
  const queryClient = useQueryClient();
  const {
    data: amenities = [],
    error: loadError,
    isPending,
  } = useQuery({
    queryKey: QUERY_KEYS.amenities,
    queryFn: amenityService.getAmenities,
  });
  const createAmenity = useMutation({
    mutationFn: amenityService.createAmenity,
    onSuccess: () => {
      setName("");
      return queryClient.invalidateQueries({ queryKey: QUERY_KEYS.amenities });
    },
  });
  const deleteAmenity = useMutation({
    mutationFn: amenityService.deleteAmenity,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.amenities }),
  });

  const handleSubmit = (event) => {
    event.preventDefault();
    createAmenity.mutate({ name: name.trim() });
  };

  return (
    <main className="container py-12 pb-28 md:py-16 md:pb-16">
      <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
        Administration
      </p>
      <h1 className="mt-3 text-4xl font-bold text-[var(--color-bark-dark)]">
        Amenities
      </h1>
      <p className="mt-3 text-base leading-7 text-[var(--color-graph)]">
        Manage the amenity options hosts can add to their properties.
      </p>

      <form
        onSubmit={handleSubmit}
        className="mt-8 flex max-w-xl flex-col gap-3 sm:flex-row"
      >
        <label className="sr-only" htmlFor="amenity-name">
          New amenity name
        </label>
        <input
          id="amenity-name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          minLength={3}
          maxLength={50}
          required
          placeholder="New amenity name"
          className="min-h-11 flex-1 rounded border border-[var(--color-bark)]/30 bg-white px-3 text-sm"
        />
        <button
          type="submit"
          disabled={createAmenity.isPending}
          className="min-h-11 rounded bg-[var(--color-bark-dark)] px-5 text-sm font-bold text-white disabled:opacity-60"
        >
          {createAmenity.isPending ? "Adding..." : "Add amenity"}
        </button>
      </form>

      {(loadError || createAmenity.error || deleteAmenity.error) && (
        <p className="mt-4 text-sm text-red-700" role="alert">
          {getErrorMessage(
            loadError || createAmenity.error || deleteAmenity.error,
          )}
        </p>
      )}

      {isPending ? (
        <p className="mt-8" role="status">
          Loading amenities...
        </p>
      ) : !loadError && amenities.length === 0 ? (
        <p className="mt-8 rounded bg-white p-5 text-sm text-[var(--color-graph)]">
          No amenities have been added yet.
        </p>
      ) : (
        !loadError && (
          <ul className="mt-8 max-w-2xl divide-y divide-[var(--color-bark)]/10 rounded border border-[var(--color-bark)]/10 bg-white">
            {amenities.map((amenity) => (
              <li
                key={amenity.id}
                className="flex items-center justify-between gap-4 px-4 py-3"
              >
                <span className="text-sm font-semibold text-[var(--color-bark-dark)]">
                  {amenity.name}
                </span>
                <button
                  type="button"
                  disabled={deleteAmenity.isPending}
                  onClick={() => {
                    if (window.confirm(`Delete the "${amenity.name}" amenity?`)) {
                      deleteAmenity.mutate(amenity.id);
                    }
                  }}
                  className="text-sm font-semibold text-red-700 disabled:opacity-60"
                >
                  Delete
                </button>
              </li>
            ))}
          </ul>
        )
      )}
    </main>
  );
}
