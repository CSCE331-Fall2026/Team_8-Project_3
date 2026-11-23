import { Routes, Route } from "react-router-dom";
import UserLayout from "./components/UserLayout";
import Cashier from "./pages/Cashier";
import Customer from "./pages/Customer";
import Kitchen from "./pages/Kitchen";
import Login from "./pages/Login";
import Manager from "./pages/Manager";

export default function App() {
  return (
    <>
        <Routes>
            <Route element={<UserLayout />}>
                <Route path="/cashier" element={<Cashier />} />
                <Route path="/customer" element={<Customer />} />
                <Route path="/staff" element={<Kitchen />} />
                <Route path="/manager" element={<Manager />} />
            </Route>
            <Route path="/" element={<Login />} />
            <Route path="*" element={<p className="p-4">Page not found</p>} />
        </Routes>
    </>
  );
}
