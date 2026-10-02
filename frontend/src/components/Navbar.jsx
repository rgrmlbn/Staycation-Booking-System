import { Link } from "react-router-dom";
import {
  FaBuilding,
  FaHome,
  FaInfoCircle,
  FaPhoneAlt,
  FaSignInAlt,
} from "react-icons/fa";
import { TbArrowBigRightLineFilled } from "react-icons/tb";
import logo from "../assets/icons/logocon-white.png";

const NAV_ICONS = {
  Home: FaHome,
  Properties: FaBuilding,
  About: FaInfoCircle,
  Contact: FaPhoneAlt,
};

export default function Navbar({
  links,
  signInTo,
  switchAreaTo,
  switchAreaLabel,
}) {
  const mobileLinks = [...links, { label: "Login", to: signInTo }];

  return (
    <>
      <header className="sticky top-0 z-50 border-b border-[var(--color-bark-dark)]/10 bg-[var(--color-white)]/90 px-12 py-4 backdrop-blur-sm max-md:px-4 max-md:py-4">
        <div className="container flex items-center justify-between">
          <Link to={links[0].to} className="flex shrink-0 items-center">
            <img src={logo} alt="Roomance" className="h-9 w-auto md:h-12" />
          </Link>

          <div className="flex items-center gap-3">
            <nav className="hidden items-center gap-10 md:mr-6 md:flex">
              {links.map((link) => {
                const Icon = NAV_ICONS[link.label];
                return (
                  <Link
                    key={link.label}
                    to={link.to}
                    className="flex items-center gap-2 text-[15px] font-semibold text-[var(--color-bark)] transition-colors hover:text-[var(--color-sand)]"
                  >
                    {Icon && <Icon aria-hidden="true" />}
                    {link.label}
                  </Link>
                );
              })}
            </nav>

            <div className="hidden items-center gap-3 md:flex">
              <Link
                to={signInTo}
                className="rounded bg-[var(--color-sand)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-ink-bark)] shadow-[var(--shadow-sm)]"
              >
                Sign in
              </Link>
              <Link
                to={switchAreaTo}
                className="flex items-center justify-center gap-2 rounded bg-[var(--color-bark)] px-5 py-2.5 text-[15px] font-semibold text-[var(--color-white)] shadow-[var(--shadow-sm)]"
              >
                {switchAreaLabel}
                <TbArrowBigRightLineFilled aria-hidden="true" />
              </Link>
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
            const Icon = NAV_ICONS[item.label] || FaSignInAlt;
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
