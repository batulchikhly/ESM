import {
  ArrowBackRounded,
  EditOutlined,
  PaymentsOutlined,
  PersonOffOutlined,
} from "@mui/icons-material";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Grid,
  Skeleton,
  Snackbar,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { EmptyState, ErrorState } from "../../components/PageState";
import {
  deactivateEmployee,
  getEmployee,
  getSalaryHistory,
  updateEmployee,
  updateSalary,
} from "../../services/employeeApi";
import type { Employee, EmploymentStatus, SalaryRecord } from "../../types/api";

export function EmployeeDetailPage() {
  const { id = "" } = useParams();
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [history, setHistory] = useState<SalaryRecord[]>([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [editOpen, setEditOpen] = useState(false);
  const [salaryOpen, setSalaryOpen] = useState(false);
  const [deactivateOpen, setDeactivateOpen] = useState(false);
  const load = () => {
    setError("");
    Promise.all([getEmployee(id), getSalaryHistory(id)])
      .then(([profile, salaryHistory]) => {
        setEmployee(profile);
        setHistory(salaryHistory);
      })
      .catch(() => setError("Employee details could not be loaded."));
  };
  useEffect(load, [id]);
  const handleDeactivate = async () => {
    try {
      const updated = await deactivateEmployee(id);
      setEmployee(updated);
      setSuccess("Employee deactivated.");
    } catch {
      setError("Employee could not be deactivated.");
    } finally {
      setDeactivateOpen(false);
    }
  };
  if (error && !employee) return <ErrorState message={error} />;
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
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        gap={2}
      >
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
        <Stack direction="row" gap={1}>
          <Button
            variant="outlined"
            startIcon={<EditOutlined />}
            onClick={() => setEditOpen(true)}
          >
            Edit
          </Button>
          {employee.employmentStatus === "ACTIVE" && (
            <Button
              color="warning"
              startIcon={<PersonOffOutlined />}
              onClick={() => setDeactivateOpen(true)}
            >
              Deactivate
            </Button>
          )}
        </Stack>
      </Stack>
      {error && <Alert severity="error">{error}</Alert>}
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
          <Stack
            direction="row"
            justifyContent="space-between"
            alignItems="center"
          >
            <Box>
              <Typography variant="h6" fontWeight={800}>
                Salary history
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Effective-dated records remain preserved.
              </Typography>
            </Box>
            <Button
              variant="contained"
              startIcon={<PaymentsOutlined />}
              onClick={() => setSalaryOpen(true)}
            >
              Update salary
            </Button>
          </Stack>
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
            <EmptyState message="No salary records." />
          )}
        </CardContent>
      </Card>
      <EditDialog
        employee={employee}
        open={editOpen}
        onClose={() => setEditOpen(false)}
        onSaved={(updated) => {
          setEmployee(updated);
          setEditOpen(false);
          setSuccess("Employee updated.");
        }}
      />
      <SalaryDialog
        employeeId={id}
        open={salaryOpen}
        onClose={() => setSalaryOpen(false)}
        onSaved={() => {
          setSalaryOpen(false);
          load();
          setSuccess("Salary updated and history refreshed.");
        }}
      />
      <Dialog open={deactivateOpen} onClose={() => setDeactivateOpen(false)}>
        <DialogTitle>Deactivate employee?</DialogTitle>
        <DialogContent>
          This keeps the employee and salary history while marking the employee
          inactive.
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeactivateOpen(false)}>Cancel</Button>
          <Button
            color="warning"
            variant="contained"
            onClick={handleDeactivate}
          >
            Deactivate
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={Boolean(success)}
        autoHideDuration={4000}
        onClose={() => setSuccess("")}
        message={success}
      />
    </Stack>
  );
}

function EditDialog({
  employee,
  open,
  onClose,
  onSaved,
}: {
  employee: Employee;
  open: boolean;
  onClose: () => void;
  onSaved: (employee: Employee) => void;
}) {
  const [form, setForm] = useState({
    firstName: employee.firstName,
    lastName: employee.lastName,
    email: employee.email,
    country: employee.country,
    department: employee.department,
    jobTitle: employee.jobTitle,
    employmentStatus: employee.employmentStatus,
  });
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  useEffect(
    () =>
      setForm({
        firstName: employee.firstName,
        lastName: employee.lastName,
        email: employee.email,
        country: employee.country,
        department: employee.department,
        jobTitle: employee.jobTitle,
        employmentStatus: employee.employmentStatus,
      }),
    [employee],
  );
  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    try {
      onSaved(await updateEmployee(employee.id, form));
    } catch {
      setError("Employee could not be updated.");
    } finally {
      setSaving(false);
    }
  };
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>Edit employee</DialogTitle>
      <DialogContent>
        <Stack
          component="form"
          id="edit-employee"
          onSubmit={submit}
          spacing={2}
          sx={{ pt: 1 }}
        >
          {error && <Alert severity="error">{error}</Alert>}
          <Grid container spacing={2}>
            {(
              [
                "firstName",
                "lastName",
                "email",
                "country",
                "department",
                "jobTitle",
              ] as const
            ).map((key) => (
              <Grid key={key} size={{ xs: 12, sm: 6 }}>
                <TextField
                  label={key.replace(/([A-Z])/g, " $1")}
                  value={form[key]}
                  onChange={(event) =>
                    setForm({ ...form, [key]: event.target.value })
                  }
                  required
                  fullWidth
                />
              </Grid>
            ))}
          </Grid>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          form="edit-employee"
          type="submit"
          variant="contained"
          disabled={saving}
        >
          {saving ? "Saving..." : "Save changes"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

function SalaryDialog({
  employeeId,
  open,
  onClose,
  onSaved,
}: {
  employeeId: string;
  open: boolean;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [annualSalary, setAnnualSalary] = useState("");
  const [currency, setCurrency] = useState("USD");
  const [effectiveFrom, setEffectiveFrom] = useState(
    new Date().toISOString().slice(0, 10),
  );
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (
      !annualSalary ||
      Number(annualSalary) < 0 ||
      !currency ||
      !effectiveFrom
    ) {
      setError("Enter a valid salary, currency, and effective date.");
      return;
    }
    setSaving(true);
    setError("");
    try {
      await updateSalary(employeeId, {
        annualSalary: Number(annualSalary),
        currency: currency.toUpperCase(),
        effectiveFrom,
      });
      onSaved();
    } catch {
      setError(
        "Salary could not be updated. The effective date may already exist.",
      );
    } finally {
      setSaving(false);
    }
  };
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Update salary</DialogTitle>
      <DialogContent>
        <Stack
          component="form"
          id="salary-update"
          onSubmit={submit}
          spacing={2}
          sx={{ pt: 1 }}
        >
          {error && <Alert severity="error">{error}</Alert>}
          <TextField
            label="Annual salary"
            type="number"
            value={annualSalary}
            onChange={(event) => setAnnualSalary(event.target.value)}
            required
            inputProps={{ min: 0 }}
          />
          <TextField
            label="Currency"
            value={currency}
            onChange={(event) => setCurrency(event.target.value)}
            required
            inputProps={{ maxLength: 3 }}
          />
          <TextField
            label="Effective from"
            type="date"
            value={effectiveFrom}
            onChange={(event) => setEffectiveFrom(event.target.value)}
            required
            InputLabelProps={{ shrink: true }}
          />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          form="salary-update"
          type="submit"
          variant="contained"
          disabled={saving}
        >
          {saving ? "Saving..." : "Update salary"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
