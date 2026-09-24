import { Alert, Box, CircularProgress, Typography } from "@mui/material";

export function LoadingState() {
  return (
    <Box sx={{ display: "grid", placeItems: "center", minHeight: 240 }}>
      <CircularProgress />
    </Box>
  );
}

export function ErrorState({
  message = "Something went wrong. Please try again.",
}: {
  message?: string;
}) {
  return (
    <Alert severity="error" sx={{ borderRadius: 2 }}>
      {message}
    </Alert>
  );
}

export function EmptyState({ message }: { message: string }) {
  return (
    <Box sx={{ py: 8, textAlign: "center" }}>
      <Typography color="text.secondary">{message}</Typography>
    </Box>
  );
}
