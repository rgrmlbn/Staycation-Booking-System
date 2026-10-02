import { Outlet, useLocation } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

export default function MainLayout() {
  const isHostArea = useLocation().pathname.startsWith("/host");
  const homePath = isHostArea ? "/host" : "/";
  const navLinks = [
    { label: "Home", to: `${homePath}#home` },
    { label: "Properties", to: `${homePath}#properties` },
    { label: "About", to: `${homePath}#about` },
    { label: "Contact", to: `${homePath}#contact` },
  ];

  return (
    <>
      <Navbar
        links={navLinks}
        signInTo={isHostArea ? "/host/login" : "/login"}
        switchAreaTo={isHostArea ? "/" : "/host"}
        switchAreaLabel={isHostArea ? "Guest Page" : "Host Page"}
      />
      <Outlet />
      <Footer />
    </>
  );
}
