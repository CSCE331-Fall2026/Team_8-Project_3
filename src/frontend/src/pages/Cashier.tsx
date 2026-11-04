import './Cashier.css'
import { Button } from "../components/ui/button";

function Cashier() {
  const testOrder = {
    orderStatus: "paid",
    paymentMethod: "credit_card",
    orderTotal: 12.5,
  };

  const handleClick = async () => {
    try {
      const res = await fetch("/api/orders", {
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
        <div className='flex-1 flex flex-row'>
          <div className='flex-1 flex flex-col'>
            <p>
                yooooa
            </p>
            <p>
                yoooo
            </p>
            <p>
                yoooo
            </p>
          </div>
          <div className='flex-1 flex flex-col'>
            <p>
                yooooa
            </p>
            <p>
                yoooo
            </p>
            <p>
                yoooo
            </p>
          </div>
          <div className='flex-1 flex flex-col'>
            <p>
                yooooa
            </p>
            <p>
                yoooo
            </p>
            <p>
                yoooo
            </p>
          </div>
        </div>
      </section>
    </>
  );
}

export default Cashier;