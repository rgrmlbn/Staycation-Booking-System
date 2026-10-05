import { Outlet, Route, Routes } from "react-router-dom";
import Footer from "../components/layouts/Footer";
import NavBar from "../components/layouts/NavBar";
import GuestPage from "../pages/guest/GuestPage";
import GuestLogin from "../pages/guest/GuestLogin";
import GuestRegister from "../pages/guest/GuestRegister";
import GuestBookings from "../pages/guest/GuestBookings";
import PropertyList from "../pages/property/PropertyList";
import PropertyDetail from "../pages/property/PropertyDetail";
import HostPage from "../pages/host/HostPage";
import HostLogin from "../pages/host/HostLogin";
import HostRegister from "../pages/host/HostRegister";
import HostProperties from "../pages/host/HostProperties";
import UnderConstruction from "../pages/under-construction/UnderConstruction";

function AppLayout() {
  return (
    <>
      <NavBar />
      <Outlet />
      <Footer />
    </>
  );
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<GuestPage />} />
        <Route path="/login" element={<GuestLogin />} />
        <Route path="/register" element={<GuestRegister />} />
        <Route path="/properties" element={<PropertyList />} />
        <Route path="/properties/:id" element={<PropertyDetail />} />
        <Route path="/guest/bookings" element={<GuestBookings />} />
        <Route path="/host" element={<HostPage />} />
        <Route path="/host/properties" element={<HostProperties />} />
        <Route path="/host/login" element={<HostLogin />} />
        <Route path="/host/register" element={<HostRegister />} />
      </Route>
      <Route path="/host/contact" element={<UnderConstruction />} />
      <Route path="*" element={<UnderConstruction />} />
    </Routes>
  );
}
