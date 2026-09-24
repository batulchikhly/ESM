import { describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { EmployeesPage } from "../features/employees/EmployeesPage";
import { EmployeeDetailPage } from "../features/employees/EmployeeDetailPage";
import * as employeeApi from "../services/employeeApi";

vi.mock("../services/employeeApi");

const employee = {
  id: "1",
  employeeCode: "EMP00001",
  firstName: "Maya",
  lastName: "Bennett",
  email: "maya@example.test",
  country: "US",
  department: "Engineering",
  jobTitle: "Engineer",
  employmentStatus: "ACTIVE" as const,
  createdAt: "2026-01-01T00:00:00Z",
  updatedAt: "2026-01-01T00:00:00Z",
  currentSalary: {
    annualSalary: 70000,
    currency: "USD",
    effectiveFrom: "2025-01-01",
  },
};
const history = [
  {
    id: "salary-1",
    employeeId: "1",
    annualSalary: 70000,
    currency: "USD",
    effectiveFrom: "2025-01-01",
    createdAt: "2026-01-01T00:00:00Z",
    createdBy: "user-1",
  },
];

describe("employee management UI", () => {
  it("renders a server-paginated employee list and sends search", async () => {
    vi.mocked(employeeApi.getEmployees).mockResolvedValue({
      content: [employee],
      page: 0,
      size: 25,
      totalElements: 1,
      totalPages: 1,
    });
    render(
      <MemoryRouter>
        <EmployeesPage />
      </MemoryRouter>,
    );
    expect(await screen.findByText("Maya Bennett")).toBeInTheDocument();
    fireEvent.change(
      screen.getByPlaceholderText("Search code, name, or email"),
      { target: { value: "Maya" } },
    );
    await waitFor(() =>
      expect(employeeApi.getEmployees).toHaveBeenCalledWith(
        expect.objectContaining({ search: "Maya" }),
      ),
    );
  });

  it("loads employee details and refreshes history after salary update", async () => {
    vi.mocked(employeeApi.getEmployee).mockResolvedValue(employee);
    vi.mocked(employeeApi.getSalaryHistory).mockResolvedValue(history);
    vi.mocked(employeeApi.updateSalary).mockResolvedValue({
      salary: {
        ...history[0],
        id: "salary-2",
        annualSalary: 75000,
        effectiveFrom: "2026-01-01",
      },
      auditId: "audit-1",
    });
    render(
      <MemoryRouter initialEntries={["/employees/1"]}>
        <EmployeeDetailPage />
      </MemoryRouter>,
    );
    expect(await screen.findByText("Maya Bennett")).toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: /update salary/i }));
    fireEvent.change(screen.getByLabelText("Annual salary"), {
      target: { value: "75000" },
    });
    fireEvent.click(screen.getByRole("button", { name: /update salary/i }));
    await waitFor(() => expect(employeeApi.updateSalary).toHaveBeenCalled());
    expect(employeeApi.getSalaryHistory).toHaveBeenCalledTimes(2);
  });

  it("confirms and deactivates an active employee", async () => {
    vi.mocked(employeeApi.getEmployee).mockResolvedValue(employee);
    vi.mocked(employeeApi.getSalaryHistory).mockResolvedValue(history);
    vi.mocked(employeeApi.deactivateEmployee).mockResolvedValue({
      ...employee,
      employmentStatus: "INACTIVE",
    });
    render(
      <MemoryRouter initialEntries={["/employees/1"]}>
        <EmployeeDetailPage />
      </MemoryRouter>,
    );
    await screen.findByText("Maya Bennett");
    fireEvent.click(screen.getByRole("button", { name: /deactivate/i }));
    fireEvent.click(screen.getByRole("button", { name: /^deactivate$/i }));
    await waitFor(() =>
      expect(employeeApi.deactivateEmployee).toHaveBeenCalledWith("1"),
    );
  });
});
