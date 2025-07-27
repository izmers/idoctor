import { useState } from "react";
import "./HomePage.css";
import "bootstrap";
import { logout, setAuthToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";

export default function HomePage() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const navigate = useNavigate();

  async function handleSubmit(
    event: React.FormEvent<HTMLFormElement>
  ): Promise<void> {
    event.preventDefault();
    console.log("login submitted");

    const credentialsObj = {
      username: username,
      password: password,
    };

    console.log(credentialsObj.username);
    console.log(credentialsObj.password);

    try {
      const response = await fetch("http://localhost:9090/authenticate", {
        method: "POST",
        body: JSON.stringify(credentialsObj),
        headers: {
          "Content-Type": "application/json",
        },
      });

      console.log("this is the response:");

      if (!response.ok) {
        throw new Error("error with response status " + response.status);
      }

      let token = response.headers.get("Authorization");
      if (token) {
        token = token.replace("Bearer ", "");
        setAuthToken(token);
      } else {
        throw new Error("Authorization header is missing");
      }

      const secondResponse = await fetch(
        `http://localhost:9090/api/userx/cred/${username}/${username}`,
        {
          method: "GET",
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      );

      if (!secondResponse) {
        throw new Error("error with response status: " + response.status);
      }

      const resData = await secondResponse.json();
      console.log("This is the resData: ");
      console.log(resData);
      const roles: string[] = resData.roles;

      if (!roles.includes("ADMIN")) {
        logout();
        throw new Error("User is not Admin");
      }
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      return;
    }

    navigate("/dashboard");
  }

  function handleUsernameChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    setUsername(event.target.value);
  }

  function handlePasswordChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    setPassword(event.target.value);
  }

  return (
    <div className="login-form-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="exampleInputEmail1" className="form-label">
            Email address or Username
          </label>
          <input
            type="text"
            className="form-control"
            id="exampleInputEmail1"
            aria-describedby="emailHelp"
            value={username}
            onChange={handleUsernameChange}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="exampleInputPassword1" className="form-label">
            Password
          </label>
          <input
            type="password"
            className="form-control"
            id="exampleInputPassword1"
            value={password}
            onChange={handlePasswordChange}
          />
        </div>
        <button type="submit" className="btn btn-dark btn-login">
          Login
        </button>
      </form>
    </div>
  );
}
