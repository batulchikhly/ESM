import { api } from "./api";
import type {
  CountryAnalytics,
  DashboardSummary,
  DepartmentAnalytics,
  SalaryBand,
} from "../types/api";

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const response = await api.get<DashboardSummary>("/dashboard/summary");
  return response.data;
}

export async function getCountryAnalytics(): Promise<CountryAnalytics[]> {
  const response = await api.get<CountryAnalytics[]>("/dashboard/by-country");
  return response.data;
}

export async function getDepartmentAnalytics(): Promise<DepartmentAnalytics[]> {
  const response = await api.get<DepartmentAnalytics[]>(
    "/dashboard/by-department",
  );
  return response.data;
}

export async function getSalaryBands(): Promise<SalaryBand[]> {
  const response = await api.get<SalaryBand[]>("/dashboard/salary-bands");
  return response.data;
}
