import { ArrowForwardRounded, SearchRounded } from "@mui/icons-material";
import {
  Alert,
  Box,
  Card,
  CardContent,
  InputAdornment,
  Pagination,
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
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getEmployees } from "../../services/employeeApi";
import type { Employee, PageResponse } from "../../types/api";

export function EmployeesPage() {
  const [data, setData] = useState<PageResponse<Employee> | null>(null);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");
  useEffect(() => {
    getEmployees(page, 25)
      .then(setData)
      .catch(() => setError("Employee data could not be loaded."));
  }, [page]);
  const filtered =
    data?.content.filter((employee) =>
      `${employee.employeeCode} ${employee.firstName} ${employee.lastName} ${employee.email}`
        .toLowerCase()
        .includes(search.toLowerCase()),
    ) ?? [];
  return (
    <Stack spacing={3}>
      <Box>
        <Typography
          variant="h4"
          sx={{ fontSize: { xs: "2rem", sm: "2.5rem" } }}
        >
          Employees
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.75 }}>
          Browse the workforce directory with server-side pagination.
        </Typography>
      </Box>
      {error && <Alert severity="error">{error}</Alert>}
      <Card>
        <CardContent sx={{ p: { xs: 1.5, sm: 3 } }}>
          <TextField
            placeholder="Search this page"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            size="small"
            sx={{ mb: 2, width: { xs: "100%", sm: 320 } }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRounded fontSize="small" />
                </InputAdornment>
              ),
            }}
          />
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
                {data ? (
                  filtered.length ? (
                    filtered.map((employee) => (
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
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={6}>
                        <Typography
                          textAlign="center"
                          color="text.secondary"
                          sx={{ py: 5 }}
                        >
                          No employees match this search.
                        </Typography>
                      </TableCell>
                    </TableRow>
                  )
                ) : (
                  [1, 2, 3, 4].map((key) => (
                    <TableRow key={key}>
                      <TableCell colSpan={6}>
                        <Skeleton />
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </Box>
          {data && (
            <Stack alignItems="center" sx={{ mt: 3 }}>
              <Pagination
                count={data.totalPages}
                page={page + 1}
                onChange={(_, value) => setPage(value - 1)}
                color="primary"
              />
            </Stack>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}
