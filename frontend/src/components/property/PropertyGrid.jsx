import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { FaBuilding } from "react-icons/fa";
import PropertyCard from "./PropertyCard";
import propertyService from "../../services/propertyService";
import { DEFAULT_PAGE_SIZE, QUERY_KEYS } from "../../utils/constants";

function getErrorMessage(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.detail ||
    error?.message ||
    "Something went wrong. Please try again."
  );
}

export default function PropertyGrid({
  pageSize,
  paginated = false,
  searchParams,
  gridClassName = "grid gap-6 sm:grid-cols-2 lg:grid-cols-4",
}) {
  const [page, setPage] = useState(0);
  const visiblePageSize = pageSize ?? DEFAULT_PAGE_SIZE;
  const queryParams = {
    page,
    size: visiblePageSize,
    ...(searchParams ?? {}),
  };
  const {
    data: propertiesPage,
    error,
    isPending,
  } = useQuery({
    queryKey: [...QUERY_KEYS.properties, "summary", queryParams],
    queryFn: () =>
      searchParams
        ? propertyService.searchSummaryProperties(queryParams)
        : propertyService.getSummaryProperties(queryParams),
  });
  const properties = propertiesPage?.content ?? [];
  const totalPages = propertiesPage?.totalPages ?? 0;

  return (
    <>
      {isPending ? (
        <p className="text-sm text-[var(--color-graph)]" role="status">
          Loading properties...
        </p>
      ) : error ? (
        <p className="text-sm text-red-700" role="alert">
          Could not load properties: {getErrorMessage(error)}
        </p>
      ) : properties.length === 0 ? (
        <div className="rounded-xl border border-[var(--color-mocha)] bg-[var(--color-white)] px-6 py-10 text-center shadow-[var(--shadow-sm)] sm:px-10">
          <span className="mx-auto flex size-14 items-center justify-center rounded-full bg-[var(--color-taste)] text-[var(--color-sun-dark)]">
            <FaBuilding aria-hidden="true" size={22} />
          </span>
          <h2 className="mt-4 text-xl font-bold text-[var(--color-bark-dark)]">
            No properties available right now
          </h2>
          <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-[var(--color-graph)]">
            There aren&apos;t any stays to show at the moment. Please check back
            soon for new places to explore.
          </p>
        </div>
      ) : (
        <div className={gridClassName}>
          {properties.map((property) => (
            <PropertyCard key={property.id} {...property} />
          ))}
        </div>
      )}
      {paginated && totalPages > 1 && (
        <nav
          aria-label="Property list pages"
          className="mt-10 flex items-center justify-center gap-4"
        >
          <button
            type="button"
            disabled={page === 0}
            onClick={() => setPage((currentPage) => currentPage - 1)}
            className="min-h-11 rounded border border-[var(--color-mocha)] px-4 py-2 text-sm font-semibold text-[var(--color-bark)] disabled:cursor-not-allowed disabled:opacity-50"
          >
            Previous
          </button>
          <span className="text-sm text-[var(--color-graph)]">
            Page {page + 1} of {totalPages}
          </span>
          <button
            type="button"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((currentPage) => currentPage + 1)}
            className="min-h-11 rounded border border-[var(--color-mocha)] px-4 py-2 text-sm font-semibold text-[var(--color-bark)] disabled:cursor-not-allowed disabled:opacity-50"
          >
            Next
          </button>
        </nav>
      )}
    </>
  );
}
