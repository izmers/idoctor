import { useEffect, useState } from "react";
import { getAuthToken } from "../utils/auth";
import "./DashoardPage.css";
import { useNavigate } from "react-router-dom";

interface User {
  fullName: string;
  email: string;
  username: string;
}

interface Doctor {
  user: User;
  status: string;
}

export default function DashboardPage() {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    async function getAllDoctors() {
      try {
        const response = await fetch("http://localhost:9090/api/doctor/all", {
          method: "GET",
          headers: {
            Authorization: "Bearer " + getAuthToken(),
          },
        });

        if (!response.ok) {
          throw new Error("error with response status: " + response.status);
        }

        const resData = await response.json();
        console.log("these are the doctors");
        console.log(resData);
        setDoctors(resData);
      } catch (error) {
        if (error instanceof Error) {
          console.error(error.message);
        } else {
          console.error("An unknown error occurred", error);
        }
        return;
      }
    }

    getAllDoctors();
  }, []);

  return (
    <div className="tableContainer">
      <table className="styled-table">
        <thead>
          <tr>
            <th scope="col">Full name</th>
            <th scope="col">E-mail</th>
            <th scope="col">Username</th>
            <th scope="col">Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {doctors.map((doctor) => {
            return (
              <tr key={doctor.user.username}>
                <td>{doctor.user.fullName}</td>
                <td>{doctor.user.email}</td>
                <td>{doctor.user.username}</td>
                <td
                  className={
                    doctor.status === "LOCKED" ? "active-row" : "inactive-row"
                  }
                >
                  {doctor.status}
                </td>
                <td>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => navigate("/doctor/" + doctor.user.username)}
                  >
                    Open
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
