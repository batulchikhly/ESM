import {
  GroupsOutlined,
  KeyboardArrowUpRounded,
  PersonOffOutlined,
  PersonOutline,
  PublicOutlined,
  ShowChartRounded,
  StackedBarChartRounded,
} from "@mui/icons-material";
import {
  Alert,
  Box,
  Card,
  CardContent,
  Chip,
  Grid,
  LinearProgress,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { useCallback, useEffect, useState, type ReactNode } from "react";
import { EmptyState } from "../../components/PageState";
import {
  getCountryAnalytics,
  getDashboardSummary,
  getDepartmentAnalytics,
  getSalaryBands,
} from "../../services/dashboardApi";
import type {
  CountryAnalytics,
  DashboardSummary,
  DepartmentAnalytics,
  SalaryBand,
} from "../../types/api";

const numberFormat = new Intl.NumberFormat("en-US", {
  maximumFractionDigits: 0,
});
const decimalFormat = new Intl.NumberFormat("en-US", {
  maximumFractionDigits: 2,
});

export function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [countries, setCountries] = useState<CountryAnalytics[]>([]);
  const [departments, setDepartments] = useState<DepartmentAnalytics[]>([]);
  const [bands, setBands] = useState<SalaryBand[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadDashboard = useCallback(() => {
    setLoading(true);
    setError("");
    Promise.all([
      getDashboardSummary(),
      getCountryAnalytics(),
      getDepartmentAnalytics(),
      getSalaryBands(),
    ])
      .then(
        ([
          summaryResponse,
          countryResponse,
          departmentResponse,
          bandResponse,
        ]) => {
          setSummary(summaryResponse);
          setCountries(countryResponse);
          setDepartments(departmentResponse);
          setBands(bandResponse);
        },
      )
      .catch(() =>
        setError("Dashboard data could not be loaded. Please try again."),
      )
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => loadDashboard(), [loadDashboard]);

  return (
    <Stack spacing={3.5}>
      <Box>
        <Typography
          variant="h4"
          sx={{ fontSize: { xs: "2rem", sm: "2.5rem" } }}
        >
          Dashboard
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.75 }}>
          A current view of workforce composition and compensation.
        </Typography>
      </Box>
      {error && (
        <Alert
          severity="error"
          action={
            <button
              onClick={loadDashboard}
              style={{
                border: 0,
                background: "transparent",
                color: "inherit",
                cursor: "pointer",
                fontWeight: 700,
              }}
            >
              Retry
            </button>
          }
        >
          {error}
        </Alert>
      )}
      <MetricCards summary={summary} loading={loading} />
      <Grid container spacing={2.5}>
        <Grid size={{ xs: 12, lg: 7 }}>
          <SalaryOverview summary={summary} loading={loading} />
        </Grid>
        <Grid size={{ xs: 12, lg: 5 }}>
          <SalaryBands bands={bands} loading={loading} />
        </Grid>
        <Grid size={{ xs: 12, lg: 6 }}>
          <DistributionCard
            title="Headcount by country"
            icon={<PublicOutlined color="primary" />}
            rows={countries}
            label={(row) => `${row.country} · ${row.currency}`}
            loading={loading}
          />
        </Grid>
        <Grid size={{ xs: 12, lg: 6 }}>
          <DistributionCard
            title="Headcount by department"
            icon={<StackedBarChartRounded color="primary" />}
            rows={departments}
            label={(row) => `${row.department} · ${row.currency}`}
            loading={loading}
          />
        </Grid>
      </Grid>
    </Stack>
  );
}

function MetricCards({
  summary,
  loading,
}: {
  summary: DashboardSummary | null;
  loading: boolean;
}) {
  const metrics = summary
    ? [
        {
          label: "Total employees",
          value: summary.totalEmployees,
          icon: <GroupsOutlined />,
          color: "#0b7285",
        },
        {
          label: "Active employees",
          value: summary.activeEmployees,
          icon: <PersonOutline />,
          color: "#2f9e44",
        },
        {
          label: "Inactive employees",
          value: summary.inactiveEmployees,
          icon: <PersonOffOutlined />,
          color: "#e67700",
        },
      ]
    : [];
  return (
    <Grid container spacing={2.5}>
      {loading && !metrics.length
        ? [1, 2, 3].map((key) => (
            <Grid key={key} size={{ xs: 12, sm: 4 }}>
              <Card>
                <CardContent sx={{ p: 3 }}>
                  <Skeleton width="45%" />
                  <Skeleton width="35%" height={48} />
                </CardContent>
              </Card>
            </Grid>
          ))
        : metrics.map((metric) => (
            <Grid key={metric.label} size={{ xs: 12, sm: 4 }}>
              <Card>
                <CardContent
                  sx={{ display: "flex", alignItems: "center", gap: 2.5, p: 3 }}
                >
                  <Stack
                    sx={{
                      bgcolor: `${metric.color}18`,
                      color: metric.color,
                      p: 1.5,
                      borderRadius: 2,
                    }}
                  >
                    {metric.icon}
                  </Stack>
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      {metric.label}
                    </Typography>
                    <Typography variant="h4" fontWeight={800}>
                      {numberFormat.format(metric.value)}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
    </Grid>
  );
}

function SalaryOverview({
  summary,
  loading,
}: {
  summary: DashboardSummary | null;
  loading: boolean;
}) {
  return (
    <Card>
      <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
        <SectionHeading
          title="Salary statistics"
          subtitle="Raw values remain separated by currency."
          icon={<ShowChartRounded color="primary" />}
        />
        {loading ? (
          <Skeleton height={180} />
        ) : summary?.salaryStatistics.length ? (
          <Box sx={{ overflowX: "auto" }}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Currency</TableCell>
                  <TableCell>Employees</TableCell>
                  <TableCell>Average</TableCell>
                  <TableCell>Range</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {summary.salaryStatistics.map((item) => (
                  <TableRow key={item.currency}>
                    <TableCell>
                      <Chip
                        label={item.currency}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>
                      {numberFormat.format(item.employeeCount)}
                    </TableCell>
                    <TableCell>
                      {decimalFormat.format(item.averageSalary)}
                    </TableCell>
                    <TableCell>
                      {numberFormat.format(item.minimumSalary)} -{" "}
                      {numberFormat.format(item.maximumSalary)}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Box>
        ) : (
          <EmptyState message="No salary statistics available." />
        )}
      </CardContent>
    </Card>
  );
}

function SalaryBands({
  bands,
  loading,
}: {
  bands: SalaryBand[];
  loading: boolean;
}) {
  return (
    <Card>
      <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
        <SectionHeading
          title="Salary bands"
          subtitle="Bands are reported within each currency."
          icon={<KeyboardArrowUpRounded color="primary" />}
        />
        {loading ? (
          <Skeleton height={180} />
        ) : bands.length ? (
          <Stack spacing={2}>
            {bands.map((band) => (
              <Box key={`${band.currency}-${band.band}`}>
                <Stack
                  direction="row"
                  justifyContent="space-between"
                  sx={{ mb: 0.5 }}
                >
                  <Typography variant="body2" fontWeight={700}>
                    {band.band.replaceAll("_", " ")}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {band.currency} · {numberFormat.format(band.employeeCount)}
                  </Typography>
                </Stack>
                <LinearProgress
                  variant="determinate"
                  value={Math.min(100, band.employeeCount)}
                  sx={{ height: 8, borderRadius: 4 }}
                />
              </Box>
            ))}
          </Stack>
        ) : (
          <EmptyState message="No salary-band data available." />
        )}
      </CardContent>
    </Card>
  );
}

function DistributionCard({
  title,
  icon,
  rows,
  label,
  loading,
}: {
  title: string;
  icon: ReactNode;
  rows: Array<CountryAnalytics | DepartmentAnalytics>;
  label: (row: CountryAnalytics | DepartmentAnalytics) => string;
  loading: boolean;
}) {
  const max = Math.max(...rows.map((row) => row.employeeCount), 1);
  return (
    <Card>
      <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
        <SectionHeading
          title={title}
          subtitle="Current employee headcount"
          icon={icon}
        />
        {loading ? (
          <Skeleton height={220} />
        ) : rows.length ? (
          <Stack spacing={2}>
            {rows.map((row, index) => (
              <Box key={`${label(row)}-${index}`}>
                <Stack
                  direction="row"
                  justifyContent="space-between"
                  sx={{ mb: 0.5 }}
                >
                  <Typography variant="body2" fontWeight={700}>
                    {label(row)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {numberFormat.format(row.employeeCount)}
                  </Typography>
                </Stack>
                <LinearProgress
                  variant="determinate"
                  value={(row.employeeCount / max) * 100}
                  sx={{ height: 8, borderRadius: 4 }}
                />
              </Box>
            ))}
          </Stack>
        ) : (
          <EmptyState message="No distribution data available." />
        )}
      </CardContent>
    </Card>
  );
}

function SectionHeading({
  title,
  subtitle,
  icon,
}: {
  title: string;
  subtitle: string;
  icon: ReactNode;
}) {
  return (
    <Stack
      direction="row"
      justifyContent="space-between"
      alignItems="flex-start"
      sx={{ mb: 2.5 }}
    >
      <Box>
        <Typography variant="h6" fontWeight={800}>
          {title}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {subtitle}
        </Typography>
      </Box>
      {icon}
    </Stack>
  );
}
