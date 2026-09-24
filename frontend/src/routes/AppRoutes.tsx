import { Navigate, Outlet, Route, Routes } from "react-router-dom";
import { useAuth } from "../features/auth/AuthContext";
import { AppShell } from "../layout/AppShell";
import { DashboardPage } from "../features/dashboard/DashboardPage";
import { EmployeeDetailPage } from "../features/employees/EmployeeDetailPage";
import { EmployeesPage } from "../features/employees/EmployeesPage";
import { LoginPage } from "../features/auth/LoginPage";

function ProtectedRoute() {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? (
    <AppShell>
      <Outlet />
    </AppShell>
  ) : (
    <Navigate to="/login" replace />
  );
}

function PublicRoute() {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <Navigate to="/dashboard" replace /> : <Outlet />;
}

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<PublicRoute />}>
        <Route path="/login" element={<LoginPage />} />
      </Route>
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/employees" element={<EmployeesPage />} />
        <Route path="/employees/:id" element={<EmployeeDetailPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
