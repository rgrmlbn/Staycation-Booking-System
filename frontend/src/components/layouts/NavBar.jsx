import { useState } from "react";
import { NavLink } from "react-router-dom";
import { Menu, X } from "lucide-react";
import logo from "../../assets/icons/logocon.png";

const NAV_LINKS = [
  { label: "Home", to: "/" },
  { label: "Properties", to: "/properties" },
  { label: "About", to: "/about" },
  { label: "Contact", to: "/contact" },
];

export default function Navbar() {
  const [open, setOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-[var(--color-bark-dark)]/10 bg-[var(--color-white)]/90 px-12 py-4 backdrop-blur-sm max-md:px-4 max-md:py-4">
      <div className="container flex items-center justify-between">
        {/* Left — Logo */}
        <div className="flex items-center">
          <NavLink
            to="/"
            className="flex shrink-0 items-center"
            onClick={() => setOpen(false)}
          >
            <img src={logo} alt="StaySaya" className="h-9 w-auto md:h-12" />
          </NavLink>
        </div>

        {/* Right */}
        <div className="flex items-center gap-3">
          {/* Desktop nav */}
          <nav className="hidden items-center gap-10 md:mr-6 md:flex">
            {NAV_LINKS.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className="font-semibold text-[var(--color-bark)] transition-colors duration-300 hover:text-[var(--color-sun-dark)]"
              >
                {link.label}
              </NavLink>
            ))}
          </nav>

          {/* Desktop CTA */}
          <div className="hidden items-center gap-3 md:flex">
            <NavLink
              to="/login"
              className="rounded bg-[var(--color-sun)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-ink-bark)] shadow-[var(--shadow-sm)] transition-colors duration-300 hover:bg-[var(--color-sun-dark)]"
            >
              Sign in
            </NavLink>
            <NavLink
              to="/login"
              className="rounded bg-[var(--color-bark)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-white)] shadow-[var(--shadow-sm)] transition-colors duration-300 hover:bg-[var(--color-bark-dark)]"
            >
              Book Now
            </NavLink>
          </div>

          {/* Mobile menu toggle */}
          <button
            type="button"
            onClick={() => setOpen((prev) => !prev)}
            aria-label={open ? "Close menu" : "Open menu"}
            aria-expanded={open}
            className="inline-flex items-center justify-center rounded-[var(--radius-sm)] p-2 text-[var(--color-ink)] md:hidden"
          >
            {open ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile panel */}
      <div
        className={`overflow-hidden border-t border-[var(--color-ink-dark)]/10 bg-[var(--color-cream)] transition-[max-height] duration-300 ease-in-out md:hidden ${
          open ? "max-h-80" : "max-h-0 border-t-0"
        }`}
      >
        <nav className="container flex flex-col gap-1 py-4">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              onClick={() => setOpen(false)}
              className={({ isActive }) =>
                [
                  "rounded-[var(--radius-sm)] px-3 py-2.5 text-[15px] font-medium transition-colors",
                  isActive
                    ? "bg-[var(--color-mocha)] text-[var(--color-ink)]"
                    : "text-[var(--color-graph)] hover:bg-[var(--color-mocha)] hover:text-[var(--color-ink)]",
                ].join(" ")
              }
            >
              {link.label}
            </NavLink>
          ))}
          <NavLink
            to="/login"
            onClick={() => setOpen(false)}
            className="mt-2 rounded-[var(--radius-sm)] bg-[var(--color-sun)] px-3 py-2.5 text-center text-[15px] font-semibold text-[var(--color-ink-dark)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-sun-dark)]"
          >
            Log in
          </NavLink>
        </nav>
      </div>
    </header>
  );
}
