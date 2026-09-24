import {
  GroupsOutlined,
  PersonOffOutlined,
  PersonOutline,
  TrendingUpRounded,
} from "@mui/icons-material";
import {
  Alert,
  Box,
  Card,
  CardContent,
  Grid,
  Skeleton,
  Stack,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { getDashboardSummary } from "../../services/dashboardApi";
import type { DashboardSummary } from "../../types/api";

export function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [error, setError] = useState("");
  useEffect(() => {
    getDashboardSummary()
      .then(setSummary)
      .catch(() => setError("Dashboard data could not be loaded."));
  }, []);
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
    <Stack spacing={4}>
      <BoxTitle
        title="Dashboard"
        subtitle="A clear view of your workforce and compensation landscape."
      />
      {error && <Alert severity="error">{error}</Alert>}
      <Grid container spacing={2.5}>
        {metrics.length
          ? metrics.map((metric) => (
              <Grid key={metric.label} size={{ xs: 12, sm: 4 }}>
                <Card>
                  <CardContent
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 2.5,
                      p: 3,
                    }}
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
                        {metric.value.toLocaleString()}
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))
          : [1, 2, 3].map((key) => (
              <Grid key={key} size={{ xs: 12, sm: 4 }}>
                <Card>
                  <CardContent>
                    <Skeleton width="45%" />
                    <Skeleton width="35%" height={48} />
                  </CardContent>
                </Card>
              </Grid>
            ))}
      </Grid>
      <Card>
        <CardContent sx={{ p: 3 }}>
          <Stack
            direction="row"
            justifyContent="space-between"
            alignItems="center"
            sx={{ mb: 2 }}
          >
            <Box>
              <Typography variant="h6" fontWeight={800}>
                Salary overview
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Raw statistics stay grouped by currency.
              </Typography>
            </Box>
            <TrendingUpRounded color="primary" />
          </Stack>
          {summary?.salaryStatistics.length ? (
            <Stack spacing={1.5}>
              {summary.salaryStatistics.map((item) => (
                <Stack
                  key={item.currency}
                  direction="row"
                  justifyContent="space-between"
                  sx={{
                    py: 1,
                    borderBottom: "1px solid",
                    borderColor: "divider",
                  }}
                >
                  <Typography fontWeight={700}>{item.currency}</Typography>
                  <Typography color="text.secondary">
                    {item.employeeCount} employees · avg{" "}
                    {item.averageSalary.toLocaleString()}
                  </Typography>
                </Stack>
              ))}
            </Stack>
          ) : (
            <Typography color="text.secondary">
              No salary statistics available yet.
            </Typography>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}

function BoxTitle({ title, subtitle }: { title: string; subtitle: string }) {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontSize: { xs: "2rem", sm: "2.5rem" } }}>
        {title}
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.75 }}>
        {subtitle}
      </Typography>
    </Box>
  );
}
