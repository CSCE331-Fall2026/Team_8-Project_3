import { Routes, Route, Link } from "react-router-dom";
import Cashier from "./pages/Cashier";
import Customer from "./pages/Customer";

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/cashier" element={<Cashier />} />
        <Route path="/customer" element={<Customer />} />
        <Route path="*" element={<p className="p-4">Page not found</p>} />
      </Routes>
    </>
  );
}
