export const API_URL = "http://localhost:9090";

interface DataOptions<T> {
  request: string;
  token: string | null;
  method?: string;
  strBody?: string;
  setIsLoading?: (isLoading: boolean) => void;
  onSuccess?: (data: T) => void;
  onError?: () => void;
}
export async function request<T>({
  request,
  token,
  method = "GET",
  strBody,
  setIsLoading,
  onSuccess,
  onError,
}: DataOptions<T>): Promise<void> {
  if (setIsLoading) {
    setIsLoading(true);
  }
  try {
    const response = await fetch(request, {
      method: method,
      body: method !== "GET" && strBody ? strBody : undefined,
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
    });

    if (!response.ok) {
      throw new Error("error with response status code: " + response.status);
    }

    const resData: T = await response.json();

    if (onSuccess) {
      onSuccess(resData);
    }
  } catch (error) {
    if (error instanceof Error) {
      console.error(error.message);
    } else {
      console.error("An unknown error occurred", error);
    }

    if (onError) {
      onError();
    }
  } finally {
    if (setIsLoading) {
      setIsLoading(false);
    }
  }
}
