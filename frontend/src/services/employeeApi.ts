import { api } from "./api";
import type { Employee, PageResponse, SalaryRecord } from "../types/api";

export async function getEmployees(
  page = 0,
  size = 25,
): Promise<PageResponse<Employee>> {
  const response = await api.get<PageResponse<Employee>>("/employees", {
    params: { page, size },
  });
  return response.data;
}

export async function getEmployee(id: string): Promise<Employee> {
  const response = await api.get<Employee>(`/employees/${id}`);
  return response.data;
}

export async function getSalaryHistory(id: string): Promise<SalaryRecord[]> {
  const response = await api.get<SalaryRecord[]>(
    `/employees/${id}/salary-history`,
  );
  return response.data;
}
