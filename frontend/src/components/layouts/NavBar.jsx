import { NavLink, useLocation } from "react-router-dom";
import logo from "../../assets/icons/logocon.png";

import { TbArrowBigRightLineFilled } from "react-icons/tb";
import {
  FaHome,
  FaBuilding,
  FaInfoCircle,
  FaPhoneAlt,
  FaSignInAlt,
} from "react-icons/fa";

const NAV_LINKS = [
  { label: "Home", to: "/", icon: FaHome },
  { label: "Properties", to: "/properties", icon: FaBuilding },
  { label: "About", to: "/about", icon: FaInfoCircle },
  { label: "Contact", to: "/contact", icon: FaPhoneAlt },
];

export default function Navbar() {
  const isHostArea = useLocation().pathname.startsWith("/host");
  const hostLink = isHostArea
    ? { label: "Guest Page", to: "/", icon: FaHome }
    : { label: "Host Page", to: "/host", icon: FaBuilding };
  const mobileHomeLink = {
    label: "Home",
    to: isHostArea ? "/host" : "/",
    icon: FaHome,
  };
  const mobileLinks = [
    mobileHomeLink,
    ...NAV_LINKS.slice(1),
    { label: "Login", to: "/login", icon: FaSignInAlt },
  ];

  return (
    <>
      <header className="sticky top-0 z-50 border-b border-[var(--color-bark-dark)]/10 bg-[var(--color-white)]/90 px-12 py-4 backdrop-blur-sm max-md:px-4 max-md:py-4">
        <div className="container flex items-center justify-between">
          {/* Left — Logo */}
          <div className="flex items-center">
            <NavLink to="/" className="flex shrink-0 items-center">
              <img src={logo} alt="Roomance" className="h-9 w-auto md:h-12" />
            </NavLink>
          </div>

          {/* Right */}
          <div className="flex items-center gap-3">
            {/* Nav */}
            <nav className="hidden items-center gap-10 md:mr-6 md:flex">
              {NAV_LINKS.map((link) => {
                const Icon = link.icon;
                return (
                  <NavLink
                    key={link.to}
                    to={link.to}
                    className={({ isActive }) =>
                      `flex items-center gap-2 text-[15px] font-semibold transition-colors hover:text-[var(--color-sand)] ${
                        isActive
                          ? "text-[var(--color-sand)]"
                          : "text-[var(--color-bark)]"
                      }`
                    }
                  >
                    <Icon />
                    {link.label}
                  </NavLink>
                );
              })}
            </nav>

            {/* CTA */}
            <div className="hidden items-center gap-3 md:flex">
              <NavLink
                to="/login"
                className="rounded bg-[var(--color-sand)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-ink-bark)] shadow-[var(--shadow-sm)]"
              >
                Sign in
              </NavLink>
              <NavLink
                to={hostLink.to}
                className="flex items-center justify-center gap-2 rounded bg-[var(--color-bark)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-white)] shadow-[var(--shadow-sm)]"
              >
                {hostLink.label}
                <TbArrowBigRightLineFilled />
              </NavLink>
            </div>
          </div>
        </div>
      </header>

      <nav
        aria-label="Mobile navigation"
        className="fixed inset-x-0 bottom-0 z-50 border-t border-[var(--color-bark-dark)]/10 bg-[var(--color-white)]/95 pb-[env(safe-area-inset-bottom)] shadow-[var(--shadow-sm)] backdrop-blur-sm md:hidden"
      >
        <div className="flex">
          {mobileLinks.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.label}
                to={item.to}
                end={item.label === "Home"}
                aria-label={item.label}
                title={item.label}
                className={({ isActive }) =>
                  `flex min-h-16 min-w-0 items-center justify-center gap-2 overflow-hidden transition-all duration-200 ease-out hover:text-[var(--color-sand)] ${
                    isActive
                      ? "flex-[2] text-[var(--color-sand)]"
                      : "flex-1 text-[var(--color-graph)]"
                  }`
                }
              >
                {({ isActive }) => (
                  <>
                    <Icon className="shrink-0 text-xl" aria-hidden="true" />
                    {isActive && (
                      <span className="truncate text-sm font-semibold">
                        {item.label}
                      </span>
                    )}
                  </>
                )}
              </NavLink>
            );
          })}
        </div>
      </nav>
    </>
  );
}
