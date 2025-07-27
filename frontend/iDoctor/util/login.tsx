import { NavigationProp, useNavigation } from "@react-navigation/native";
import { AuthContextType } from "../store/auth-context";
import { RootStackParamList } from "../App";
import { API_URL } from "./requests";

interface LoginCredentials {
  username: string;
  password: string;
}

interface User {
  userIsDoctor: boolean;
}

export async function handleLogin(
  credentials: LoginCredentials,
  authCtx: AuthContextType,
  setIsLoading: (isLoading: boolean) => void,
  navigation: NavigationProp<RootStackParamList>,
  setInvalid?: (invalid: boolean) => void
): Promise<void> {
  try {
    setIsLoading(true);
    setInvalid?.(false);

    const response = await fetch(`${API_URL}/authenticate`, {
      method: "POST",
      body: JSON.stringify(credentials),
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      setInvalid?.(true);
      throw new Error("Error with response status code: " + response.status);
    }

    const token = response.headers.get("Authorization")?.replace("Bearer ", "");
    if (!token) {
      throw new Error("Token is undefined");
    }

    authCtx.authenticate(token);
    authCtx.assignUsername(credentials.username);
    setIsLoading(false);

    const response2 = await fetch(`${API_URL}/api/userx/current`, {
      method: "GET",
      headers: {
        Authorization: "Bearer " + token,
      },
    });

    if (!response2.ok) {
      throw new Error("error with response status code: " + response2.status);
    }

    const resData: User = await response2.json();
    authCtx.assignUserType(resData.userIsDoctor);
  } catch (error) {
    if (error instanceof Error) {
      console.error(error.message);
    } else {
      console.error("An unknown error occurred", error);
    }
    setIsLoading(false);
    return;
  }

  navigation.navigate("Main");
}
