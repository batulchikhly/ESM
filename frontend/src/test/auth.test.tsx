import { describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../features/auth/AuthContext";
import { LoginPage } from "../features/auth/LoginPage";
import * as authApi from "../services/authApi";

vi.mock("../services/authApi");

describe("login flow", () => {
  it("stores a successful session and redirects", async () => {
    vi.mocked(authApi.login).mockResolvedValue({
      accessToken: "token",
      tokenType: "Bearer",
      expiresAt: "2030-01-01T00:00:00Z",
      email: "hr@example.test",
      role: "HR_MANAGER",
    });
    render(
      <MemoryRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </MemoryRouter>,
    );
    fireEvent.click(screen.getByRole("button", { name: /sign in/i }));
    await waitFor(() =>
      expect(localStorage.getItem("acme.auth")).toContain("token"),
    );
  });

  it("shows an invalid credential error", async () => {
    vi.mocked(authApi.login).mockRejectedValue({
      response: { data: { message: "Invalid email or password" } },
    });
    render(
      <MemoryRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </MemoryRouter>,
    );
    fireEvent.click(screen.getByRole("button", { name: /sign in/i }));
    expect(
      await screen.findByText("Invalid email or password"),
    ).toBeInTheDocument();
  });
});
