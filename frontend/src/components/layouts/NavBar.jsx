import { useState } from "react";
import { NavLink } from "react-router-dom";
import { Menu, X } from "lucide-react";
import logo from "../../assets/icons/logocon.png";

const NAV_LINKS = [
  { label: "Home", to: "/" },
  { label: "Properties", to: "/properties" },
  { label: "About", to: "/about" },
];


export default function Navbar() {
  const [open, setOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-[var(--color-mocha)] bg-[var(--color-cream)]/90 backdrop-blur-sm">
      <div className="container flex h-18 items-center justify-between">
        {/* Logo */}
        <NavLink to="/" className="flex shrink-0 items-center" onClick={() => setOpen(false)}>
          <img src={logo} alt="StaySaya" className="h-9 w-auto md:h-12" />
        </NavLink>

        {/* Desktop nav */}
        <nav className="hidden md:flex md:items-center md:gap-10">
          {NAV_LINKS.map((link) => (
            <NavLink>
              {link.label}
            </NavLink>
          ))}
        </nav>

        {/* Desktop CTA */}
        <div className="hidden md:block">
          <NavLink
            to="/login"
            className="rounded-[var(--radius-sm)] bg-[var(--color-sun)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-ink-dark)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-sun-dark)]"
          >
            Log in
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

      {/* Mobile panel */}
      <div
        className={`overflow-hidden border-t border-[var(--color-mocha)] bg-[var(--color-cream)] transition-[max-height] duration-300 ease-in-out md:hidden ${
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