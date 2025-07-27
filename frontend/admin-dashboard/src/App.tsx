import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./App.css";
import HomePage from "./components/HomePage";
import "bootstrap/dist/css/bootstrap.min.css";
import DashboardPage from "./components/DashboardPage";
import DoctorPage from "./components/DoctorPage";

const router = createBrowserRouter([
  { path: "/", element: <HomePage /> },
  { path: "/dashboard", element: <DashboardPage /> },
  { path: "/doctor/:username", element: <DoctorPage /> },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
