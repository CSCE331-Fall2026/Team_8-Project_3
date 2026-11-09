import './Customer.css'
import { Button } from "../components/ui/button";
import { API_URL } from "../lib/api";

function Customer() {
  const testOrder = {
    orderStatus: "paid",
    paymentMethod: "credit_card",
    orderTotal: 12.5,
  };

  const handleClick = async () => {
    try {
      const res = await fetch(`${API_URL}/orders`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(testOrder),
      });
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      const text = await res.text();
      console.log("Response:", text);
    } catch (e) {
      console.error("Request failed:", e);
    }
  };

  return (
    <>
      <section id="center">
        <div className="flex items-center p-4">
          <Button onClick={handleClick}>Click this button to create a test order</Button>
        </div>
      </section>
    </>
  );
}
export default Customer;