import { Link } from "react-router-dom";

const ADMIN_SECTIONS = [
  {
    title: "Users",
    description: "Review registered guest, host, and admin accounts.",
    to: "/admin/users",
  },
  {
    title: "Amenities",
    description: "Manage the amenities available for properties.",
    to: "/admin/amenities",
  },
  {
    title: "Permissions",
    description: "Review the capabilities assigned to each account role.",
    to: "/admin/permissions",
  },
];

export default function Dashboard() {
  return (
    <main className="container py-12 pb-28 md:py-16 md:pb-16">
      <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
        Admin dashboard
      </p>
      <h1 className="mt-3 text-4xl font-bold text-[var(--color-bark-dark)]">
        Platform overview
      </h1>
      <p className="mt-3 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
        Manage Roomance accounts, amenities, and role access from one place.
      </p>
      <div className="mt-8 grid gap-5 md:grid-cols-3">
        {ADMIN_SECTIONS.map((section) => (
          <Link
            key={section.to}
            to={section.to}
            className="rounded border border-[var(--color-bark)]/10 bg-[var(--color-white)] p-6 shadow-[var(--shadow-sm)] transition hover:-translate-y-0.5"
          >
            <h2 className="text-xl font-bold text-[var(--color-bark-dark)]">
              {section.title}
            </h2>
            <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
              {section.description}
            </p>
            <span className="mt-5 inline-block text-sm font-bold text-[var(--color-sun-dark)]">
              Open {section.title.toLowerCase()}
            </span>
          </Link>
        ))}
      </div>
    </main>
  );
}
