import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";
import { AuthProvider } from "./features/auth/AuthContext";
import { AppRoutes } from "./routes/AppRoutes";
import "./styles.css";

const theme = createTheme({
  palette: {
    primary: { main: "#0b7285", dark: "#075985" },
    secondary: { main: "#f08c46" },
    background: { default: "#f5f7fa", paper: "#ffffff" },
  },
  typography: {
    fontFamily: '"DM Sans", "Segoe UI", sans-serif',
    h1: { fontWeight: 800 },
    h2: { fontWeight: 800 },
    h3: { fontWeight: 800 },
  },
  shape: { borderRadius: 12 },
  components: {
    MuiButton: { defaultProps: { disableElevation: true } },
    MuiCard: {
      styleOverrides: {
        root: {
          border: "1px solid #e7edf2",
          boxShadow: "0 8px 30px rgba(16,42,67,.05)",
        },
      },
    },
  },
});

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  </React.StrictMode>,
);
