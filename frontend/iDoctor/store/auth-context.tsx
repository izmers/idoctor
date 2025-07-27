import React, { createContext, useState } from "react";

export interface AuthContextType {
  token: string | null;
  username: string;
  isAuthenticated: boolean;
  isDoctor: boolean;
  authenticate: (token: string) => void;
  assignUsername: (username: string) => void;
  assignUserType: (value: boolean) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
  token: null,
  username: "",
  isAuthenticated: false,
  isDoctor: false,
  authenticate: () => {},
  assignUsername: () => {},
  assignUserType: () => {},
  logout: () => {},
});

interface AuthContextProviderProps {
  children: React.ReactNode;
}

export default function AuthContextProvider({
  children,
}: AuthContextProviderProps) {
  const [authToken, setAuthToken] = useState<string | null>(null);
  const [username, setUsername] = useState<string>("");
  const [isDoctor, setIsDoctor] = useState<boolean>(false);

  function authenticate(token: string) {
    setAuthToken(token);
  }

  function assignUsername(username: string) {
    setUsername(username);
  }

  function assignUserType(value: boolean) {
    setIsDoctor(value);
  }

  function logout() {
    isDoctor && setIsDoctor(false);
    setAuthToken(null);
  }

  const value: AuthContextType = {
    token: authToken,
    username: username,
    isAuthenticated: !!authToken,
    isDoctor: isDoctor,
    authenticate,
    assignUsername,
    assignUserType,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
