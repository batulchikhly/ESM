import { ArrowBackRounded } from "@mui/icons-material";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
  Skeleton,
  Stack,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getEmployee, getSalaryHistory } from "../../services/employeeApi";
import type { Employee, SalaryRecord } from "../../types/api";

export function EmployeeDetailPage() {
  const { id = "" } = useParams();
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [history, setHistory] = useState<SalaryRecord[]>([]);
  const [error, setError] = useState("");
  useEffect(() => {
    Promise.all([getEmployee(id), getSalaryHistory(id)])
      .then(([profile, salaryHistory]) => {
        setEmployee(profile);
        setHistory(salaryHistory);
      })
      .catch(() => setError("Employee details could not be loaded."));
  }, [id]);
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!employee)
    return (
      <Stack spacing={2}>
        <Skeleton height={60} />
        <Skeleton height={220} />
      </Stack>
    );
  return (
    <Stack spacing={3}>
      <Button
        component={Link}
        to="/employees"
        startIcon={<ArrowBackRounded />}
        sx={{ alignSelf: "flex-start" }}
      >
        Back to employees
      </Button>
      <BoxTitle employee={employee} />
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Typography variant="h6" fontWeight={800} sx={{ mb: 2 }}>
            Profile
          </Typography>
          <Grid container spacing={2}>
            {[
              ["Email", employee.email],
              ["Department", employee.department],
              ["Job title", employee.jobTitle],
              ["Country", employee.country],
              ["Status", employee.employmentStatus],
            ].map(([label, value]) => (
              <Grid key={label} size={{ xs: 12, sm: 6, md: 4 }}>
                <Typography variant="caption" color="text.secondary">
                  {label}
                </Typography>
                <Typography fontWeight={700}>{value}</Typography>
              </Grid>
            ))}
          </Grid>
        </CardContent>
      </Card>
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Typography variant="h6" fontWeight={800}>
            Salary history
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Effective-dated records remain preserved.
          </Typography>
          {history.length ? (
            <Stack divider={<Divider />} spacing={0}>
              {history.map((record) => (
                <Stack
                  key={record.id}
                  direction="row"
                  justifyContent="space-between"
                  sx={{ py: 1.5 }}
                >
                  <Box>
                    <Typography fontWeight={700}>
                      {record.currency} {record.annualSalary.toLocaleString()}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Effective {record.effectiveFrom}
                    </Typography>
                  </Box>
                  <Chip label="Recorded" size="small" variant="outlined" />
                </Stack>
              ))}
            </Stack>
          ) : (
            <Typography color="text.secondary">No salary records.</Typography>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}

function BoxTitle({ employee }: { employee: Employee }) {
  return (
    <Box>
      <Stack direction="row" spacing={1.5} alignItems="center">
        <Typography
          variant="h4"
          sx={{ fontSize: { xs: "2rem", sm: "2.5rem" } }}
        >
          {employee.firstName} {employee.lastName}
        </Typography>
        <Chip label={employee.employeeCode} />
      </Stack>
      <Typography color="text.secondary" sx={{ mt: 0.75 }}>
        {employee.jobTitle} · {employee.department}
      </Typography>
    </Box>
  );
}
