import { Routes, Route } from "react-router-dom";
import Cashier from "./pages/Cashier";
import Customer from "./pages/Customer";
import Kitchen from "./pages/Kitchen";
import Manager from "./pages/Manager";

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/cashier" element={<Cashier />} />
        <Route path="/customer" element={<Customer />} />
        <Route path="/staff" element={<Kitchen />} />
        <Route path="/manager" element={<Manager />} />
        <Route path="*" element={<p className="p-4">Page not found</p>} />
      </Routes>
    </>
  );
}
