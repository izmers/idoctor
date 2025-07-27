import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getAuthToken } from "../utils/auth";
import { Worker } from "@react-pdf-viewer/core";
import { Viewer } from "@react-pdf-viewer/core";
import "@react-pdf-viewer/core/lib/styles/index.css";
import { defaultLayoutPlugin } from "@react-pdf-viewer/default-layout";
import "@react-pdf-viewer/default-layout/lib/styles/index.css";
import "./DoctorPage.css";

interface Document {
  id: number;
  name: string;
  type: string;
}

interface Doctor {
  fullName: string;
  email: string;
}

export default function DoctorPage() {
  const defaultLayoutPluginInstance = defaultLayoutPlugin();
  const [documents, setDocuments] = useState<Document[]>([]);
  const [doctor, setDoctor] = useState<Doctor>({ fullName: "", email: "" });
  const [showPdf, setShowPdf] = useState(false);
  const [url, setUrl] = useState("");
  const [emailBody, setEmailBody] = useState("");
  const params = useParams();

  useEffect(() => {
    async function loadDocuments() {
      try {
        const response = await fetch(
          `http://localhost:9090/api/document/doctor/${params.username}`,
          {
            method: "GET",
            headers: {
              Authorization: "Bearer " + getAuthToken(),
            },
          }
        );

        if (!response.ok) {
          throw new Error("error with response status: " + response.status);
        }

        const resData = await response.json();
        setDocuments(resData);
      } catch (error) {
        if (error instanceof Error) {
          console.error(error.message);
        } else {
          console.error("An unknown error occurred", error);
        }
        return;
      }
    }

    loadDocuments();
  }, []);

  useEffect(() => {
    async function loadDoctor() {
      try {
        const response = await fetch(
          `http://localhost:9090/api/userx/username/${params.username}`,
          {
            method: "GET",
            headers: {
              Authorization: "Bearer " + getAuthToken(),
            },
          }
        );

        if (!response.ok) {
          throw new Error("error with response status: " + response.status);
        }

        const resData = await response.json();
        setDoctor(resData);
      } catch (error) {
        if (error instanceof Error) {
          console.error(error.message);
        } else {
          console.error("An unknown error occurred", error);
        }
        return;
      }
    }
    loadDoctor();
  }, []);

  async function handleViewDocument(id: number) {
    try {
      const response = await fetch(
        `http://localhost:9090/api/document/view/${id}`,
        {
          method: "GET",
          headers: {
            Authorization: "Bearer " + getAuthToken(),
          },
        }
      );

      if (!response.ok) {
        throw new Error("error with response status: " + response.status);
      }

      console.log("called");
      console.log("Document response data:");
      const resDataArrayBuffer = await response.arrayBuffer();
      console.log("Document response data arraybuffer:");
      console.log(resDataArrayBuffer);

      const blob = new Blob([resDataArrayBuffer], {
        type: "application/pdf",
      });

      const url = URL.createObjectURL(blob);
      setUrl(url);
      console.log("this is the url: ");
      console.log(url);
      setShowPdf(true);
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      return;
    }
  }

  async function handleSendEmail(type: string): Promise<void> {
    if (type === "accept") {
      console.log("accepted");
    } else if (type === "decline") {
      console.log("declined");
    }
    console.log(emailBody);

    const emailBodyObj = {
      host: "smtp.gmail.com",
      port: 587,
      username: "remzi.cetin64@gmail.com",
      password: "secret",
      from: "remzi.cetin64@gmail.com",
      to: doctor.email,
      subject:
        type === "accept"
          ? "Confirmation of Documents Acceptance"
          : "Confirmation of Documents Rejection",
      body: emailBody,
    };

    try {
      const response = await fetch(
        `http://localhost:9090/api/email/send/${type}`,
        {
          method: "POST",
          body: JSON.stringify(emailBodyObj),
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + getAuthToken(),
          },
        }
      );

      if (!response.ok) {
        throw new Error("error with response status: " + response.status);
      }
      console.log("email sent");
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      return;
    }
  }

  function handleChangeEmailBody(
    event: React.ChangeEvent<HTMLTextAreaElement>
  ): void {
    console.log(emailBody);
    setEmailBody(event.target.value);
  }

  return (
    <div className="doctor-page-container">
      <div className="doctor-info">
        <h1>{doctor.fullName}</h1>
        <h3>{doctor.email}</h3>

        <div className="documents">
          {documents.map((doc) => {
            return (
              <div className="doc" key={doc.id}>
                <p style={{ margin: 10, fontSize: 20 }}>{doc.name}</p>
                <button
                  type="button"
                  className="btn btn-primary own-btn"
                  onClick={() => handleViewDocument(doc.id)}
                >
                  Open
                </button>
                <button
                  type="button"
                  className="btn btn-secondary own-btn"
                  onClick={() => setShowPdf(false)}
                >
                  Close
                </button>
              </div>
            );
          })}
        </div>
      </div>

      <div className="email-container">
        <div className="mb-3">
          <label htmlFor="exampleFormControlTextarea1" className="form-label">
            Email Content:
          </label>
          <textarea
            className="form-control"
            id="exampleFormControlTextarea1"
            rows={3}
            value={emailBody}
            onChange={handleChangeEmailBody}
          ></textarea>
          <div className="accept-decline">
            <button
              type="button"
              className="btn btn-success own-btn"
              onClick={() => handleSendEmail("accept")}
            >
              Accept
            </button>
            <button
              type="button"
              className="btn btn-danger own-btn"
              onClick={() => handleSendEmail("decline")}
            >
              Decline
            </button>
          </div>
        </div>
      </div>

      {showPdf && (
        <Worker workerUrl="https://unpkg.com/pdfjs-dist@3.4.120/build/pdf.worker.min.js">
          <Viewer fileUrl={url} plugins={[defaultLayoutPluginInstance]} />
        </Worker>
      )}
    </div>
  );
}
