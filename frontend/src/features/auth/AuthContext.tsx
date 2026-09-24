import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { login as loginRequest } from "../../services/authApi";
import {
  authStorageKey,
  registerUnauthorizedHandler,
} from "../../services/api";
import type { LoginRequest, LoginResponse } from "../../types/api";

type AuthContextValue = {
  session: LoginResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (request: LoginRequest) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<LoginResponse | null>(() => {
    const stored = localStorage.getItem(authStorageKey);
    return stored ? (JSON.parse(stored) as LoginResponse) : null;
  });
  const [isLoading, setIsLoading] = useState(false);

  const logout = () => {
    localStorage.removeItem(authStorageKey);
    setSession(null);
  };

  useEffect(() => {
    registerUnauthorizedHandler(logout);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      isAuthenticated: Boolean(session?.accessToken),
      isLoading,
      async login(request) {
        setIsLoading(true);
        try {
          const response = await loginRequest(request);
          localStorage.setItem(authStorageKey, JSON.stringify(response));
          setSession(response);
        } finally {
          setIsLoading(false);
        }
      },
      logout,
    }),
    [isLoading, session],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}
