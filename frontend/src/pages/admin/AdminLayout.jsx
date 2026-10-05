import { NavLink, Outlet } from "react-router-dom";
import logo from "../../assets/icons/logocon-white.png";
import AccountMenu from "../../components/layouts/AccountMenu";
import { useAuth } from "../../hooks/useAuth";

const ADMIN_LINKS = [
  { label: "Dashboard", to: "/admin/dashboard" },
  { label: "Users", to: "/admin/users" },
  { label: "Amenities", to: "/admin/amenities" },
  { label: "Permissions", to: "/admin/permissions" },
];

export default function AdminLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-[var(--color-cream)]">
      <header className="border-b border-[var(--color-bark-dark)]/10 bg-[var(--color-white)]">
        <div className="container flex flex-wrap items-center justify-between gap-x-4 gap-y-3 py-4">
          <div className="flex items-center gap-3">
            <img src={logo} alt="Roomance" className="h-8 w-auto sm:h-10" />
          </div>
          <div className="flex items-center gap-3">
            <nav aria-label="Admin navigation" className="flex flex-wrap gap-2">
              {ADMIN_LINKS.map(({ label, to }) => (
                <NavLink
                  key={to}
                  to={to}
                  className={({ isActive }) =>
                    `rounded px-3 py-2 text-sm font-semibold ${
                      isActive
                        ? "bg-[var(--color-bark-dark)] text-white"
                        : "text-[var(--color-bark-dark)] hover:bg-[var(--color-bark-dark)]/10"
                    }`
                  }
                >
                  {label}
                </NavLink>
              ))}
            </nav>
            <AccountMenu user={user} logout={logout} showAccountLinks={false} />
          </div>
        </div>
      </header>
      <Outlet />
    </div>
  );
}
