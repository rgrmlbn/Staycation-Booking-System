import { Route, Routes } from "react-router-dom";
import GuestPage from "../pages/guest/GuestPage";
import GuestLogin from "../pages/guest/GuestLogin";
import GuestRegister from "../pages/guest/GuestRegister";
import HostPage from "../pages/host/HostPage";
import HostLogin from "../pages/host/HostLogin";
import HostRegister from "../pages/host/HostRegister";
import UnderConstruction from "../pages/under-construction/UnderConstruction";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<GuestPage />} />
      <Route path="/login" element={<GuestLogin />} />
      <Route path="/register" element={<GuestRegister />} />
      <Route path="/host" element={<HostPage />} />
      <Route path="/host/login" element={<HostLogin />} />
      <Route path="/host/register" element={<HostRegister />} />
      <Route path="*" element={<UnderConstruction />} />
    </Routes>
  );
}
