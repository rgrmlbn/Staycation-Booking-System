import { Navigate, Outlet } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import { useAuth } from "../hooks/useAuth";

export default function RoleRoute({ roles = [], children }) {
  const { user } = useAuth();
  const allowedRoles = (Array.isArray(roles) ? roles : [roles]).map((role) =>
    role.toUpperCase(),
  );
  const userRole = user?.role?.toUpperCase();
  const redirectTo = allowedRoles.includes("HOST") ? "/host/login" : "/login";

  return (
    <ProtectedRoute redirectTo={redirectTo}>
      {allowedRoles.length === 0 || allowedRoles.includes(userRole) ? (
        children ?? <Outlet />
      ) : (
        <Navigate to="/" replace />
      )}
    </ProtectedRoute>
  );
}
