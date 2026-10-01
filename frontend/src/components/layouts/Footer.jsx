import { Link } from "react-router-dom";
import {
  FaBuilding,
  FaCookieBite,
  FaEnvelope,
  FaFileAlt,
  FaHome,
  FaPhoneAlt,
  FaShieldAlt,
  FaSignInAlt,
  FaUserPlus,
} from "react-icons/fa";
import logo from "../../assets/icons/logocon-dark.png";

const FOOTER_GROUPS = [
  {
    title: "Explore",
    links: [
      { label: "Home", to: "/", icon: FaHome },
      { label: "Properties", to: "/properties", icon: FaBuilding },
      { label: "Become a host", to: "/host", icon: FaUserPlus },
    ],
  },
  {
    title: "Your account",
    links: [
      { label: "Guest sign in", to: "/login", icon: FaSignInAlt },
      { label: "Create an account", to: "/register", icon: FaUserPlus },
      { label: "Host sign in", to: "/host/login", icon: FaSignInAlt },
    ],
  },
  {
    title: "Legal",
    links: [
      { label: "Privacy Policy", to: "/privacy-policy", icon: FaShieldAlt },
      { label: "Terms of Service", to: "/terms-of-service", icon: FaFileAlt },
      { label: "Cookie Policy", to: "/cookie-policy", icon: FaCookieBite },
    ],
  },
];

export default function Footer() {
  return (
    <footer className="border-t-4 border-[var(--color-sun)] bg-[var(--color-bark-dark)] text-white">
      <div className="container grid gap-10 px-6 pb-28 pt-12 sm:grid-cols-2 md:grid-cols-3 md:px-10 md:pb-12 md:pt-14 xl:grid-cols-[1.5fr_repeat(4,minmax(0,1fr))]">
        <div className="max-w-sm">
          <Link to="/" aria-label="Roomance home" className="inline-flex">
            <img src={logo} alt="Roomance" className="h-11 w-auto" />
          </Link>
          <p className="mt-5 text-sm leading-6 text-white/70">
            Find a place to settle in, slow down, and feel at home.
          </p>
        </div>

        {FOOTER_GROUPS.map((group) => (
          <div key={group.title}>
            <h2 className="text-sm font-bold text-[var(--color-sand)]">
              {group.title}
            </h2>
            <ul className="mt-4 space-y-3">
              {group.links.map((link) => (
                <li key={link.to}>
                  <Link
                    to={link.to}
                    className="inline-flex items-center gap-2 text-sm text-white/75 transition-colors hover:text-white"
                  >
                    <link.icon
                      aria-hidden="true"
                      className="shrink-0 text-[var(--color-sand)]"
                    />
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        ))}

        <div>
          <h2 className="text-sm font-bold text-[var(--color-sand)]">
            Contact
          </h2>
          <ul className="mt-4 space-y-3 text-sm text-white/75">
            <li>
              <a
                href="tel:+63281234567"
                className="inline-flex items-center gap-2 transition-colors hover:text-white"
              >
                <FaPhoneAlt
                  aria-hidden="true"
                  className="text-[var(--color-sand)]"
                />
                +63 (2) 8123-4567
              </a>
            </li>
            <li>
              <a
                href="mailto:roomance@gmail.com"
                className="inline-flex items-center gap-2 transition-colors hover:text-white"
              >
                <FaEnvelope
                  aria-hidden="true"
                  className="text-[var(--color-sand)]"
                />
                roomance@gmail.com
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div className="border-t border-white/15">
        <div className="container flex flex-col gap-2 px-6 py-5 text-xs text-white/60 sm:flex-row sm:items-center sm:justify-between md:px-10">
          <span>
            © {new Date().getFullYear()} Roomance. All rights reserved.
          </span>
          <span>Made for your next stay.</span>
        </div>
      </div>
    </footer>
  );
}
