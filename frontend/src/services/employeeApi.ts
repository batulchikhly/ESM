import { api } from "./api";
import type {
  Employee,
  EmployeeCreateRequest,
  EmployeeUpdateRequest,
  EmploymentStatus,
  PageResponse,
  SalaryRecord,
  SalaryUpdateRequest,
} from "../types/api";

export interface EmployeeListParams {
  page: number;
  size: number;
  search?: string;
  country?: string;
  department?: string;
  currency?: string;
  status?: EmploymentStatus;
  minSalary?: string;
  maxSalary?: string;
  sort?: string;
}

export async function getEmployees(
  params: EmployeeListParams,
): Promise<PageResponse<Employee>> {
  const response = await api.get<PageResponse<Employee>>("/employees", {
    params,
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

export async function createEmployee(
  request: EmployeeCreateRequest,
): Promise<Employee> {
  const response = await api.post<Employee>("/employees", request);
  return response.data;
}

export async function updateEmployee(
  id: string,
  request: EmployeeUpdateRequest,
): Promise<Employee> {
  const response = await api.put<Employee>(`/employees/${id}`, request);
  return response.data;
}

export async function deactivateEmployee(id: string): Promise<Employee> {
  const response = await api.patch<Employee>(`/employees/${id}/deactivate`);
  return response.data;
}

export async function updateSalary(
  id: string,
  request: SalaryUpdateRequest,
): Promise<{ salary: SalaryRecord; auditId: string }> {
  const response = await api.post<{ salary: SalaryRecord; auditId: string }>(
    `/employees/${id}/salary`,
    request,
  );
  return response.data;
}
