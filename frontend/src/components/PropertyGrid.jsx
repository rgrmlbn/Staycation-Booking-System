import { useState } from "react";
import PropertyCard from "./PropertyCard";
import PROPERTIES from "../data/properties";

export default function PropertyGrid({
  pageSize,
  paginated = false,
  gridClassName = "grid gap-6 sm:grid-cols-2 lg:grid-cols-4",
}) {
  const [page, setPage] = useState(0);
  const visiblePageSize = pageSize ?? PROPERTIES.length;
  const totalPages = Math.ceil(PROPERTIES.length / visiblePageSize);
  const properties = PROPERTIES.slice(
    page * visiblePageSize,
    (page + 1) * visiblePageSize,
  );

  return (
    <>
      <div className={gridClassName}>
        {properties.map((property) => (
          <PropertyCard key={property.id} {...property} />
        ))}
      </div>
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
