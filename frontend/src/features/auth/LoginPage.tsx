import { LockOutlined, LoginRounded } from "@mui/icons-material";
import {
  Alert,
  Box,
  Button,
  Card,
  Container,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";
import type { ApiError } from "../../types/api";

export function LoginPage() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("seed-admin@example.test");
  const [password, setPassword] = useState("DemoPassword123!");
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    try {
      await login({ email, password });
      navigate("/dashboard", { replace: true });
    } catch (reason) {
      const apiError = reason as { response?: { data?: ApiError } };
      setError(
        apiError.response?.data?.message ??
          "Unable to sign in. Check your credentials.",
      );
    }
  }

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "grid",
        placeItems: "center",
        background:
          "linear-gradient(135deg, #e6fcf5 0%, #f8fafc 50%, #fff4e6 100%)",
        px: 2,
      }}
    >
      <Container maxWidth="sm">
        <Stack spacing={3} alignItems="center" sx={{ mb: 4 }}>
          <Box
            sx={{
              width: 60,
              height: 60,
              borderRadius: 3,
              bgcolor: "primary.main",
              color: "white",
              display: "grid",
              placeItems: "center",
              boxShadow: "0 14px 30px rgba(11,114,133,.25)",
            }}
          >
            <LockOutlined fontSize="large" />
          </Box>
          <Box textAlign="center">
            <Typography
              variant="h3"
              sx={{ fontSize: { xs: "2rem", sm: "2.6rem" } }}
            >
              Welcome back
            </Typography>
            <Typography color="text.secondary" sx={{ mt: 1 }}>
              Compensation operations, without the spreadsheet maze.
            </Typography>
          </Box>
        </Stack>
        <Card sx={{ p: { xs: 3, sm: 5 } }}>
          <Stack component="form" spacing={2.5} onSubmit={handleSubmit}>
            <Box>
              <Typography variant="h6" fontWeight={800}>
                HR Manager sign in
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Use your authorized ACME workspace account.
              </Typography>
            </Box>
            {error && <Alert severity="error">{error}</Alert>}
            <TextField
              label="Email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
              fullWidth
              autoComplete="email"
            />
            <TextField
              label="Password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              fullWidth
              autoComplete="current-password"
            />
            <Button
              type="submit"
              size="large"
              variant="contained"
              endIcon={<LoginRounded />}
              disabled={isLoading}
            >
              {isLoading ? "Signing in..." : "Sign in"}
            </Button>
          </Stack>
        </Card>
        <Typography
          variant="caption"
          color="text.secondary"
          display="block"
          textAlign="center"
          sx={{ mt: 3 }}
        >
          ACME internal compensation workspace
        </Typography>
      </Container>
    </Box>
  );
}
