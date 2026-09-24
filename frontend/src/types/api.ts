export type EmploymentStatus = "ACTIVE" | "INACTIVE";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresAt: string;
  email: string;
  role: string;
}

export interface SalarySummary {
  annualSalary: number;
  currency: string;
  effectiveFrom: string;
}

export interface Employee {
  id: string;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  country: string;
  department: string;
  jobTitle: string;
  employmentStatus: EmploymentStatus;
  createdAt: string;
  updatedAt: string;
  currentSalary: SalarySummary | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface SalaryRecord {
  id: string;
  employeeId: string;
  annualSalary: number;
  currency: string;
  effectiveFrom: string;
  createdAt: string;
  createdBy: string;
}

export interface SalaryStatistics {
  currency: string;
  employeeCount: number;
  minimumSalary: number;
  maximumSalary: number;
  averageSalary: number;
}

export interface DashboardSummary {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;
  salaryStatistics: SalaryStatistics[];
}

export interface CountryAnalytics {
  country: string;
  currency: string;
  employeeCount: number;
  minimumSalary: number;
  maximumSalary: number;
  averageSalary: number;
}

export interface DepartmentAnalytics {
  department: string;
  currency: string;
  employeeCount: number;
  minimumSalary: number;
  maximumSalary: number;
  averageSalary: number;
}

export interface SalaryBand {
  currency: string;
  band: string;
  employeeCount: number;
}

export interface ApiError {
  code?: string;
  message?: string;
}
