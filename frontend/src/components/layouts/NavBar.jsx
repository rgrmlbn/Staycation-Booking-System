import { useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import logo from "../../assets/icons/logocon-white.png";
import AccountMenu from "./AccountMenu";

import { TbArrowBigRightLineFilled } from "react-icons/tb";
import {
  FaHome,
  FaBuilding,
  FaInfoCircle,
  FaPhoneAlt,
  FaSignInAlt,
  FaUserCircle,
} from "react-icons/fa";
import { useAuth } from "../../hooks/useAuth";

const NAV_LINKS = [
  { label: "Home", to: "/", icon: FaHome },
  { label: "Properties", to: "/properties", icon: FaBuilding },
  { label: "About", to: "/#about", icon: FaInfoCircle },
  { label: "Contact", to: "/contact", icon: FaPhoneAlt },
];

export default function Navbar() {
  const location = useLocation();
  const { user, isAuthenticated, isLoading, logout } = useAuth();
  const isHostArea = location.pathname.startsWith("/host");
  const loginPath = isHostArea ? "/host/login" : "/login";

  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: "instant" });
  }, [location.pathname]);

  useEffect(() => {
    if (!location.hash) return undefined;

    const sectionId = decodeURIComponent(location.hash.slice(1));
    const frame = window.requestAnimationFrame(() => {
      if (sectionId === "home") {
        window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
        return;
      }

      document.getElementById(sectionId)?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    });

    return () => window.cancelAnimationFrame(frame);
  }, [location.hash, location.pathname]);

  const homeLink = {
    ...NAV_LINKS[0],
    to: isHostArea ? "/host#home" : "/#home",
  };
  const hostLink = isHostArea
    ? { label: "Guest Page", to: "/#home", icon: FaHome }
    : { label: "Host Page", to: "/host#home", icon: FaBuilding };
  const propertyLink = isHostArea
    ? { ...NAV_LINKS[1], to: "/host#properties" }
    : { ...NAV_LINKS[1], to: "/#properties" };
  const aboutLink = {
    ...NAV_LINKS[2],
    to: isHostArea ? "/host#about" : "/#about",
  };
  const contactLink = {
    ...NAV_LINKS[3],
    to: isHostArea ? "/host#contact" : "/#contact",
  };
  const navLinks = [homeLink, propertyLink, aboutLink, contactLink];
  const mobileLinks = [
    homeLink,
    ...navLinks.slice(1),
    {
      label: isAuthenticated ? "Profile" : "Login",
      to: isAuthenticated ? "/account/profile" : loginPath,
      icon: isAuthenticated ? FaUserCircle : FaSignInAlt,
    },
  ];

  return (
    <>
      <header className="sticky top-0 z-50 border-b border-[var(--color-bark-dark)]/10 bg-[var(--color-white)]/90 py-4 backdrop-blur-sm max-md:py-4">
        <div className="container flex items-center justify-between">
          {/* Left — Logo */}
          <div className="flex items-center">
            <Link to={homeLink.to} className="flex shrink-0 items-center">
              <img src={logo} alt="Roomance" className="h-9 w-auto md:h-12" />
            </Link>
          </div>

          {/* Right */}
          <div className="flex items-center gap-3">
            {/* Nav */}
            <nav className="hidden items-center gap-10 md:mr-6 md:flex">
              {navLinks.map((link) => {
                const Icon = link.icon;
                return (
                  <Link
                    key={link.to}
                    to={link.to}
                    className="flex items-center gap-2 text-[15px] font-semibold text-[var(--color-bark)] transition-colors hover:text-[var(--color-sand)]"
                  >
                    <Icon />
                    {link.label}
                  </Link>
                );
              })}
            </nav>

            {/* CTA */}
            <div
              className={`items-center gap-3 ${
                isAuthenticated ? "flex" : "hidden md:flex"
              }`}
            >
              {isAuthenticated ? (
                <AccountMenu user={user} logout={logout} />
              ) : isLoading ? (
                <div
                  aria-label="Loading account"
                  className="size-11 animate-pulse rounded-full bg-[var(--color-bark)]/10"
                />
              ) : (
                <>
                  <Link
                    to={loginPath}
                    className="rounded bg-[var(--color-sand)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-ink-bark)] shadow-[var(--shadow-sm)]"
                  >
                    Sign in
                  </Link>
                  <Link
                    to={hostLink.to}
                    className="flex items-center justify-center gap-2 rounded bg-[var(--color-bark)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-white)] shadow-[var(--shadow-sm)]"
                  >
                    {hostLink.label}
                    <TbArrowBigRightLineFilled />
                  </Link>
                </>
              )}
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
              <Link
                key={item.label}
                to={item.to}
                aria-label={item.label}
                title={item.label}
                className="flex min-h-16 min-w-0 flex-1 flex-col items-center justify-center gap-1 overflow-hidden text-[var(--color-graph)] transition-colors hover:text-[var(--color-sand)]"
              >
                <Icon className="shrink-0 text-xl" aria-hidden="true" />
                <span className="max-w-full truncate text-[10px] font-semibold">
                  {item.label}
                </span>
              </Link>
            );
          })}
        </div>
      </nav>
    </>
  );
}
