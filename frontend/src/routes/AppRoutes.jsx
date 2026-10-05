import { Outlet, Route, Routes } from "react-router-dom";
import Footer from "../components/layouts/Footer";
import NavBar from "../components/layouts/NavBar";
import GuestPage from "../pages/guest/GuestPage";
import GuestLogin from "../pages/guest/GuestLogin";
import GuestRegister from "../pages/guest/GuestRegister";
import GuestBookings from "../pages/guest/GuestBookings";
import GuestDashboard from "../pages/guest/GuestDashboard";
import PropertyList from "../pages/property/PropertyList";
import PropertyDetail from "../pages/property/PropertyDetail";
import HostPage from "../pages/host/HostPage";
import HostLogin from "../pages/host/HostLogin";
import HostRegister from "../pages/host/HostRegister";
import HostProperties from "../pages/host/HostProperties";
import HostDashboard from "../pages/host/HostDashboard";
import Login from "../pages/auth/Login";
import AdminLayout from "../pages/admin/AdminLayout";
import AdminDashboard from "../pages/admin/Dashboard";
import AdminUsers from "../pages/admin/Users";
import AdminAmenities from "../pages/admin/Amenities";
import AdminPermissions from "../pages/admin/Permissions";
import Profile from "../pages/account/Profile";
import Settings from "../pages/account/Settings";
import UnderConstruction from "../pages/under-construction/UnderConstruction";
import ProtectedRoute from "./ProtectedRoute";
import RoleRoute from "./RoleRoute";

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
        <Route path="/admin/login" element={<Login audience="admin" />} />
        <Route path="/register" element={<GuestRegister />} />
        <Route path="/properties" element={<PropertyList />} />
        <Route path="/properties/:id" element={<PropertyDetail />} />
        <Route
          path="/account/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/account/settings"
          element={
            <ProtectedRoute>
              <Settings />
            </ProtectedRoute>
          }
        />
        <Route
          path="/guest/dashboard"
          element={
            <RoleRoute roles="GUEST">
              <GuestDashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/guest/bookings"
          element={
            <RoleRoute roles="GUEST">
              <GuestBookings />
            </RoleRoute>
          }
        />
        <Route path="/host" element={<HostPage />} />
        <Route
          path="/host/dashboard"
          element={
            <RoleRoute roles="HOST">
              <HostDashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/host/properties"
          element={
            <RoleRoute roles="HOST">
              <HostProperties />
            </RoleRoute>
          }
        />
        <Route path="/host/login" element={<HostLogin />} />
        <Route path="/host/register" element={<HostRegister />} />
      </Route>
      <Route element={<RoleRoute roles="ADMIN"><AdminLayout /></RoleRoute>}>
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/admin/users" element={<AdminUsers />} />
        <Route path="/admin/amenities" element={<AdminAmenities />} />
        <Route path="/admin/permissions" element={<AdminPermissions />} />
      </Route>
      <Route path="/host/contact" element={<UnderConstruction />} />
      <Route path="*" element={<UnderConstruction />} />
    </Routes>
  );
}
