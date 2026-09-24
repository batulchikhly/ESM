import {
  AssessmentOutlined,
  GroupsOutlined,
  Logout,
  MenuRounded,
} from "@mui/icons-material";
import {
  AppBar,
  Box,
  Button,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
} from "@mui/material";
import { useState, type ReactNode } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../features/auth/AuthContext";

const navItems = [
  { label: "Dashboard", path: "/dashboard", icon: <AssessmentOutlined /> },
  { label: "Employees", path: "/employees", icon: <GroupsOutlined /> },
];

export function AppShell({ children }: { children: ReactNode }) {
  const [drawerOpen, setDrawerOpen] = useState(false);
  const { session, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const drawer = (
    <Box sx={{ width: 260, pt: 2 }}>
      <Box sx={{ px: 3, pb: 3 }}>
        <Typography variant="overline" color="primary">
          ACME / PEOPLE OPS
        </Typography>
        <Typography variant="h6" fontWeight={800}>
          Salary management
        </Typography>
      </Box>
      <List>
        {navItems.map((item) => (
          <ListItemButton
            key={item.path}
            component={NavLink}
            to={item.path}
            selected={location.pathname.startsWith(item.path)}
            onClick={() => setDrawerOpen(false)}
            sx={{ mx: 1, borderRadius: 2 }}
          >
            <ListItemIcon>{item.icon}</ListItemIcon>
            <ListItemText primary={item.label} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: "flex", minHeight: "100vh", bgcolor: "#f5f7fa" }}>
      <AppBar
        position="fixed"
        elevation={0}
        color="inherit"
        sx={{
          borderBottom: "1px solid",
          borderColor: "divider",
          bgcolor: "rgba(255,255,255,.92)",
          backdropFilter: "blur(12px)",
        }}
      >
        <Toolbar sx={{ pl: { md: "292px" } }}>
          <IconButton
            onClick={() => setDrawerOpen(true)}
            sx={{ display: { md: "none" }, mr: 1 }}
          >
            <MenuRounded />
          </IconButton>
          <Typography variant="subtitle1" fontWeight={800} sx={{ flexGrow: 1 }}>
            Operations workspace
          </Typography>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mr: 2, display: { xs: "none", sm: "block" } }}
          >
            {session?.email}
          </Typography>
          <Button
            color="inherit"
            startIcon={<Logout />}
            onClick={() => {
              logout();
              navigate("/login");
            }}
          >
            Logout
          </Button>
        </Toolbar>
      </AppBar>
      <Box component="nav" sx={{ width: { md: 260 }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="temporary"
          open={drawerOpen}
          onClose={() => setDrawerOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{ display: { xs: "block", md: "none" } }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          open
          sx={{
            display: { xs: "none", md: "block" },
            "& .MuiDrawer-paper": {
              width: 260,
              boxSizing: "border-box",
              borderRight: 0,
            },
          }}
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          px: { xs: 2, sm: 4 },
          py: 4,
          pt: 11,
          maxWidth: 1440,
          mx: "auto",
          width: "100%",
        }}
      >
        {children}
      </Box>
    </Box>
  );
}
