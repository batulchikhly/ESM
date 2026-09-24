import {
  AddRounded,
  ArrowForwardRounded,
  FilterAltOutlined,
  SearchRounded,
} from "@mui/icons-material";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  FormControl,
  InputAdornment,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState, type FormEvent } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { EmptyState, ErrorState } from "../../components/PageState";
import {
  createEmployee,
  getEmployees,
  type EmployeeListParams,
} from "../../services/employeeApi";
import type { Employee, EmploymentStatus, PageResponse } from "../../types/api";

export function EmployeesPage() {
  const [query, setQuery] = useSearchParams();
  const [data, setData] = useState<PageResponse<Employee> | null>(null);
  const [error, setError] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [formOpen, setFormOpen] = useState(false);
  const params: EmployeeListParams = {
    page: Number(query.get("page") ?? 0),
    size: 25,
    search: query.get("search") || undefined,
    country: query.get("country") || undefined,
    department: query.get("department") || undefined,
    currency: query.get("currency") || undefined,
    status: (query.get("status") as EmploymentStatus | null) ?? undefined,
    sort: query.get("sort") ?? "lastName,asc",
  };
  useEffect(() => {
    setError("");
    getEmployees(params)
      .then(setData)
      .catch(() => setError("Employee data could not be loaded."));
    // Query parameters are the server-side fetch boundary.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [query]);
  const setFilter = (key: string, value: string) => {
    const next = new URLSearchParams(query);
    if (value) next.set(key, value);
    else next.delete(key);
    if (key !== "page") next.set("page", "0");
    setQuery(next);
  };
  const clearFilters = () =>
    setQuery(
      new URLSearchParams({ page: "0", size: "25", sort: "lastName,asc" }),
    );

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        gap={2}
      >
        <Box>
          <Typography
            variant="h4"
            sx={{ fontSize: { xs: "2rem", sm: "2.5rem" } }}
          >
            Employees
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.75 }}>
            Search and manage the workforce directory.
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddRounded />}
          onClick={() => setFormOpen(true)}
        >
          Add employee
        </Button>
      </Stack>
      {error && <ErrorState message={error} />}
      <Card>
        <CardContent sx={{ p: { xs: 1.5, sm: 3 } }}>
          <Stack
            direction={{ xs: "column", md: "row" }}
            spacing={1.5}
            sx={{ mb: 2 }}
          >
            <TextField
              placeholder="Search code, name, or email"
              value={query.get("search") ?? ""}
              onChange={(event) => setFilter("search", event.target.value)}
              size="small"
              fullWidth
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchRounded fontSize="small" />
                  </InputAdornment>
                ),
              }}
            />
            <Button
              variant="outlined"
              startIcon={<FilterAltOutlined />}
              onClick={() => setShowFilters((value) => !value)}
            >
              Filters
            </Button>
            <Button onClick={clearFilters}>Clear</Button>
          </Stack>
          {showFilters && (
            <Stack
              direction={{ xs: "column", sm: "row" }}
              flexWrap="wrap"
              gap={1.5}
              sx={{ mb: 2 }}
            >
              <TextField
                label="Country"
                size="small"
                value={query.get("country") ?? ""}
                onChange={(event) =>
                  setFilter("country", event.target.value.toUpperCase())
                }
                sx={{ minWidth: 130 }}
              />
              <TextField
                label="Department"
                size="small"
                value={query.get("department") ?? ""}
                onChange={(event) =>
                  setFilter("department", event.target.value)
                }
                sx={{ minWidth: 160 }}
              />
              <TextField
                label="Currency"
                size="small"
                value={query.get("currency") ?? ""}
                onChange={(event) =>
                  setFilter("currency", event.target.value.toUpperCase())
                }
                sx={{ minWidth: 130 }}
              />
              <FormControl size="small" sx={{ minWidth: 150 }}>
                <InputLabel>Status</InputLabel>
                <Select
                  label="Status"
                  value={query.get("status") ?? ""}
                  onChange={(event) => setFilter("status", event.target.value)}
                >
                  <MenuItem value="">All statuses</MenuItem>
                  <MenuItem value="ACTIVE">Active</MenuItem>
                  <MenuItem value="INACTIVE">Inactive</MenuItem>
                </Select>
              </FormControl>
              <FormControl size="small" sx={{ minWidth: 170 }}>
                <InputLabel>Sort</InputLabel>
                <Select
                  label="Sort"
                  value={query.get("sort") ?? "lastName,asc"}
                  onChange={(event) => setFilter("sort", event.target.value)}
                >
                  <MenuItem value="lastName,asc">Last name A-Z</MenuItem>
                  <MenuItem value="lastName,desc">Last name Z-A</MenuItem>
                  <MenuItem value="employeeCode,asc">Employee code</MenuItem>
                  <MenuItem value="department,asc">Department</MenuItem>
                </Select>
              </FormControl>
            </Stack>
          )}
          <Box sx={{ overflowX: "auto" }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Employee</TableCell>
                  <TableCell>Department</TableCell>
                  <TableCell>Country</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Current salary</TableCell>
                  <TableCell />
                </TableRow>
              </TableHead>
              <TableBody>
                {data?.content.map((employee) => (
                  <TableRow key={employee.id} hover>
                    <TableCell>
                      <Typography fontWeight={700}>
                        {employee.firstName} {employee.lastName}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {employee.employeeCode} · {employee.email}
                      </Typography>
                    </TableCell>
                    <TableCell>{employee.department}</TableCell>
                    <TableCell>{employee.country}</TableCell>
                    <TableCell>
                      <Typography
                        color={
                          employee.employmentStatus === "ACTIVE"
                            ? "success.main"
                            : "warning.main"
                        }
                        fontWeight={700}
                      >
                        {employee.employmentStatus}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      {employee.currentSalary
                        ? `${employee.currentSalary.currency} ${employee.currentSalary.annualSalary.toLocaleString()}`
                        : "—"}
                    </TableCell>
                    <TableCell>
                      <Link to={`/employees/${employee.id}`}>
                        <ArrowForwardRounded color="primary" />
                      </Link>
                    </TableCell>
                  </TableRow>
                ))}
                {!data &&
                  !error &&
                  [1, 2, 3, 4].map((key) => (
                    <TableRow key={key}>
                      <TableCell colSpan={6}>
                        <Skeleton />
                      </TableCell>
                    </TableRow>
                  ))}
                {data?.content.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={6}>
                      <EmptyState message="No employees match these filters." />
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </Box>
          {data && (
            <Stack alignItems="center" sx={{ mt: 3 }}>
              <Pagination
                count={data.totalPages}
                page={data.page + 1}
                onChange={(_, value) => setFilter("page", String(value - 1))}
                color="primary"
              />
            </Stack>
          )}
        </CardContent>
      </Card>
      {formOpen && (
        <EmployeeForm
          onClose={() => setFormOpen(false)}
          onSaved={() => {
            setFormOpen(false);
            clearFilters();
          }}
        />
      )}
    </Stack>
  );
}

function EmployeeForm({
  onClose,
  onSaved,
}: {
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState({
    employeeCode: "",
    firstName: "",
    lastName: "",
    email: "",
    country: "",
    department: "",
    jobTitle: "",
    employmentStatus: "ACTIVE" as EmploymentStatus,
    annualSalary: "",
    currency: "USD",
    effectiveFrom: new Date().toISOString().slice(0, 10),
  });
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const update = (key: string, value: string) =>
    setForm((current) => ({ ...current, [key]: value }));
  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    if (!form.annualSalary || Number(form.annualSalary) < 0) {
      setError("Enter a non-negative salary.");
      return;
    }
    setSaving(true);
    try {
      await createEmployee({
        employeeCode: form.employeeCode,
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        country: form.country,
        department: form.department,
        jobTitle: form.jobTitle,
        employmentStatus: form.employmentStatus,
        initialSalary: {
          annualSalary: Number(form.annualSalary),
          currency: form.currency,
          effectiveFrom: form.effectiveFrom,
        },
      });
      onSaved();
    } catch {
      setError(
        "Employee could not be created. Check the fields and try again.",
      );
    } finally {
      setSaving(false);
    }
  };
  return (
    <Card sx={{ p: 3 }}>
      <Stack component="form" onSubmit={submit} spacing={2}>
        <Typography variant="h6" fontWeight={800}>
          Add employee
        </Typography>
        {error && <Alert severity="error">{error}</Alert>}
        <Stack direction={{ xs: "column", sm: "row" }} gap={2}>
          {(["employeeCode", "firstName", "lastName", "email"] as const).map(
            (key) => (
              <TextField
                key={key}
                label={key.replace(/([A-Z])/g, " $1")}
                value={form[key]}
                onChange={(event) => update(key, event.target.value)}
                required
                fullWidth
              />
            ),
          )}
        </Stack>
        <Stack direction={{ xs: "column", sm: "row" }} gap={2}>
          {(
            [
              "country",
              "department",
              "jobTitle",
              "currency",
              "annualSalary",
              "effectiveFrom",
            ] as const
          ).map((key) => (
            <TextField
              key={key}
              label={key.replace(/([A-Z])/g, " $1")}
              type={
                key === "annualSalary"
                  ? "number"
                  : key === "effectiveFrom"
                    ? "date"
                    : "text"
              }
              value={form[key]}
              onChange={(event) => update(key, event.target.value)}
              required
              fullWidth
              InputLabelProps={
                key === "effectiveFrom" ? { shrink: true } : undefined
              }
            />
          ))}
        </Stack>
        <Stack direction="row" justifyContent="flex-end" gap={1}>
          <Button onClick={onClose}>Cancel</Button>
          <Button type="submit" variant="contained" disabled={saving}>
            {saving ? "Saving..." : "Create employee"}
          </Button>
        </Stack>
      </Stack>
    </Card>
  );
}
