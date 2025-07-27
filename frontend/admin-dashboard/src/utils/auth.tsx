import { redirect } from "react-router-dom";

export function getAuthToken(): string | null {
  return localStorage.getItem("token");
}

export function setAuthToken(token: string): void {
  localStorage.setItem("token", token);
}

export function logout() {
  localStorage.removeItem("token");
}

export function checkAuthLoader(): Response | null {
  const token: string | null = getAuthToken();

  if (!token) {
    return redirect("/");
  }
  return null;
}
