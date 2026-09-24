import { describe, expect, it } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../features/auth/AuthContext";
import { AppRoutes } from "../routes/AppRoutes";

describe("protected routes", () => {
  it("redirects guests to login", () => {
    localStorage.clear();
    render(
      <MemoryRouter initialEntries={["/employees"]}>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </MemoryRouter>,
    );
    expect(screen.getByText("Welcome back")).toBeInTheDocument();
  });
});
