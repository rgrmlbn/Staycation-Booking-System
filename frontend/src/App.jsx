import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import NavBar from "./components/layouts/NavBar";
import Footer from "./components/layouts/Footer";
import AppRoutes from "./routes/AppRoutes";

function App() {
  const location = useLocation();
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!isLoading) return undefined;

    const timeout = window.setTimeout(() => setIsLoading(false), 450);
    return () => window.clearTimeout(timeout);
  }, [isLoading, location.key]);

  useEffect(() => {
    if (!location.hash) return undefined;

    const sectionId = decodeURIComponent(location.hash.slice(1));
    const frame = window.requestAnimationFrame(() => {
      document.getElementById(sectionId)?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    });

    return () => window.cancelAnimationFrame(frame);
  }, [location.hash, location.pathname]);

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
      <NavBar />
      <AppRoutes />
      <Footer />
      {isLoading && (
        <div
          className="fixed inset-0 z-[100] flex flex-col items-center justify-center gap-4 bg-white text-sm font-semibold text-[var(--color-bark-dark)]"
          role="status"
          aria-live="polite"
        >
          <span
            aria-hidden="true"
            className="size-8 animate-spin rounded-full border-[3px] border-[var(--color-sun)] border-t-transparent"
          />
          Loading...
        </div>
      )}
    </div>
  );
}

export default App;
