import { useEffect, useRef, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { FaCog, FaSignOutAlt, FaUserEdit } from "react-icons/fa";

export default function AccountMenu({
  user,
  logout,
  showAccountLinks = true,
}) {
  const navigate = useNavigate();
  const location = useLocation();
  const menuRef = useRef(null);
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  const initials =
    user?.name
      ?.trim()
      .split(/\s+/)
      .slice(0, 2)
      .map((part) => part[0])
      .join("")
      .toUpperCase() || "U";

  useEffect(() => {
    menuRef.current?.removeAttribute("open");
  }, [location.pathname]);

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      await logout();
    } catch (error) {
      window.alert(
        error.response?.data?.message ||
          "Your session was cleared, but the server could not be notified.",
      );
    } finally {
      setIsLoggingOut(false);
      navigate("/", { replace: true });
    }
  };

  return (
    <details ref={menuRef} className="group relative">
      <summary
        aria-label={`Open ${user?.name || "account"} menu`}
        className="flex size-11 cursor-pointer list-none items-center justify-center rounded-full border-2 border-[var(--color-sand)] bg-[var(--color-bark)] text-[var(--color-white)] shadow-[var(--shadow-sm)] transition hover:bg-[var(--color-bark-dark)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--color-sun-dark)] [&::-webkit-details-marker]:hidden"
      >
        <span aria-hidden="true" className="text-sm font-bold">
          {initials}
        </span>
      </summary>
      <div className="absolute right-0 top-full z-50 mt-3 w-72 max-w-[calc(100vw-2rem)] overflow-hidden rounded-lg border border-[var(--color-mocha)] bg-[var(--color-white)] shadow-[var(--shadow-lg)]">
        <div className="flex items-center gap-3 bg-[var(--color-cream)] px-4 py-4">
          <div
            aria-hidden="true"
            className="flex size-12 shrink-0 items-center justify-center rounded-full bg-[var(--color-sand)] text-sm font-bold text-[var(--color-bark-dark)]"
          >
            {initials}
          </div>
          <div className="min-w-0">
            <p className="truncate text-sm font-bold text-[var(--color-bark-dark)]">
              {user?.name}
            </p>
            <p className="truncate text-xs text-[var(--color-graph)]">
              {user?.email}
            </p>
            <p className="mt-1 text-[10px] font-bold uppercase tracking-[0.12em] text-[var(--color-sun-dark)]">
              {user?.role?.toLowerCase()}
            </p>
          </div>
        </div>
        <div className="p-2">
          {showAccountLinks && (
            <>
              <p className="px-3 pb-1 pt-2 text-[10px] font-bold uppercase tracking-[0.16em] text-[var(--color-graph)]">
                Account
              </p>
              <Link
                to="/account/profile"
                className="flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-semibold text-[var(--color-bark-dark)] transition hover:bg-[var(--color-cream)] focus-visible:bg-[var(--color-cream)] focus-visible:outline-none"
              >
                <FaUserEdit
                  aria-hidden="true"
                  className="text-[var(--color-sun-dark)]"
                />
                <span>Edit profile</span>
              </Link>
              <Link
                to="/account/settings"
                className="flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-semibold text-[var(--color-bark-dark)] transition hover:bg-[var(--color-cream)] focus-visible:bg-[var(--color-cream)] focus-visible:outline-none"
              >
                <FaCog
                  aria-hidden="true"
                  className="text-[var(--color-sun-dark)]"
                />
                <span>Settings</span>
              </Link>
            </>
          )}
          {showAccountLinks && (
            <div className="my-2 border-t border-[var(--color-mocha)]/60" />
          )}
          {!showAccountLinks && (
            <p className="px-3 pb-1 pt-2 text-[10px] font-bold uppercase tracking-[0.16em] text-[var(--color-graph)]">
              Administration
            </p>
          )}
          <button
            type="button"
            disabled={isLoggingOut}
            onClick={handleLogout}
            className="flex w-full items-center gap-3 rounded-md px-3 py-2.5 text-left text-sm font-semibold text-red-700 transition hover:bg-red-50 focus-visible:bg-red-50 focus-visible:outline-none disabled:opacity-60"
          >
            <FaSignOutAlt aria-hidden="true" />
            <span>{isLoggingOut ? "Signing out..." : "Log out"}</span>
          </button>
        </div>
      </div>
    </details>
  );
}
