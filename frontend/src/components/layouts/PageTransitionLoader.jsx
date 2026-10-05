import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import logo from "../../assets/icons/logocon-white.png";

export default function PageTransitionLoader({ children }) {
  const location = useLocation();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!isLoading) return undefined;

    const timeout = window.setTimeout(() => setIsLoading(false), 850);
    return () => window.clearTimeout(timeout);
  }, [isLoading, location.key]);

  useEffect(() => {
    if (!isLoading) return undefined;

    const previousOverflow = document.documentElement.style.overflow;
    document.documentElement.style.overflow = "hidden";

    return () => {
      document.documentElement.style.overflow = previousOverflow;
    };
  }, [isLoading]);

  const handleNavigationClick = (event) => {
    if (!(event.target instanceof Element)) return;

    const link = event.target.closest("a[href]");
    if (!link || link.target || link.hasAttribute("download")) return;

    const destination = new URL(link.href, window.location.href);
    const current = new URL(window.location.href);
    const isSameLocation =
      destination.origin === current.origin &&
      destination.pathname === current.pathname &&
      destination.search === current.search &&
      destination.hash === current.hash;
    const isHomeSectionNavigation =
      current.pathname === destination.pathname &&
      ["/", "/host"].includes(current.pathname) &&
      ["#home", "#properties", "#about", "#contact"].includes(destination.hash);

    if (
      destination.origin === current.origin &&
      !isSameLocation &&
      !isHomeSectionNavigation
    ) {
      setIsLoading(true);
    }
  };

  return (
    <div onClickCapture={handleNavigationClick}>
      {children}
      {isLoading && (
        <div
          className="fixed inset-0 z-[100] flex items-center justify-center bg-[var(--color-cream)]/95 p-6 backdrop-blur-sm"
          role="status"
          aria-live="polite"
          aria-busy="true"
        >
          <div className="w-full max-w-sm overflow-hidden rounded-3xl border border-[var(--color-bark)]/10 bg-[var(--color-white)] shadow-[var(--shadow-xl)]">
            <div className="flex flex-col items-center px-8 pb-8 pt-9 text-center">
              <img src={logo} alt="Roomance" className="h-10 w-auto" />
              <p className="mt-2 text-xs font-bold uppercase tracking-[0.2em] text-[var(--color-palm-dark)]">
                Your next stay starts here
              </p>

              <div className="relative mt-8 flex h-32 w-full items-end justify-center overflow-hidden rounded-2xl bg-[var(--color-taste)]">
                <span
                  aria-hidden="true"
                  className="absolute left-1/2 top-5 size-14 -translate-x-1/2 rounded-full bg-[var(--color-sun)] shadow-[0_0_32px_rgba(248,168,5,0.35)]"
                />
                <svg
                  aria-hidden="true"
                  viewBox="0 0 320 120"
                  className="absolute inset-x-0 bottom-0 h-24 w-full"
                  preserveAspectRatio="none"
                >
                  <path
                    d="M0 66C58 37 99 42 154 68s111 29 166-2v54H0Z"
                    fill="var(--color-palm)"
                  />
                  <path
                    d="M0 83c58-21 112-14 164 8s106 10 156-9v38H0Z"
                    fill="var(--color-palm-dark)"
                  />
                </svg>
                <span className="relative z-10 mb-3 size-2 animate-bounce rounded-full bg-[var(--color-white)] shadow-[0_0_0_5px_var(--color-palm-dark)]" />
              </div>

              <p className="mt-6 text-lg font-bold text-[var(--color-bark-dark)]">
                Finding your place...
              </p>
              <p className="mt-1 text-sm text-[var(--color-graph)]">
                Making yourself at home
              </p>
              <span
                aria-hidden="true"
                className="mt-5 h-1 w-20 overflow-hidden rounded-full bg-[var(--color-mocha)]"
              >
                <span className="loader-progress-fill block h-full rounded-full bg-[var(--color-sun)]" />
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
