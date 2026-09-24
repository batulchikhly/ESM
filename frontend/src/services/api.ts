import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const authStorageKey = "acme.auth";

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const stored = localStorage.getItem(authStorageKey);
  if (stored) {
    const auth = JSON.parse(stored) as { accessToken?: string };
    if (auth.accessToken) {
      config.headers.Authorization = `Bearer ${auth.accessToken}`;
    }
  }
  return config;
});

export function registerUnauthorizedHandler(handler: () => void) {
  api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        handler();
      }
      return Promise.reject(error);
    },
  );
}
