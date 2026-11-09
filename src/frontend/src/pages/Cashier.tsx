import { useState } from "react";
import { Button } from "../components/ui/button";
import { Checkbox } from "../components/ui/checkbox";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/card";

const ADD_BUTTONS = ["Bowl", "Plate", "Bigger Plate", "Single Item", "Drink/App"];
const ENTREES = ["Chicken", "Fried Rice", "Orange Chicken"];

function Cashier() {
  // Placeholder cart: a single item row
  const [item] = useState({ name: "Bowl", price: 8.5 });
  const [quantity, setQuantity] = useState(1);
  const [selected, setSelected] = useState<string[]>([]);

  const total = item.price * quantity;

  const toggleEntree = (name: string) =>
    setSelected((prev) =>
      prev.includes(name) ? prev.filter((n) => n !== name) : [...prev, name]
    );

  const handleClear = () => {
    setQuantity(0);
    setSelected([]);
  };

  const handleCheckout = async () => {
    try {
      const res = await fetch("/api/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          orderStatus: "paid",
          paymentMethod: "credit_card",
          orderTotal: total,
        }),
      });
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      console.log("Response:", await res.text());
    } catch (e) {
      console.error("Request failed:", e);
    }
  };

  return (
    <div className="flex h-screen w-full flex-row gap-4 p-4">
      {/* Column 1: cart */}
      <div className="flex flex-1 flex-col gap-3">
        <h2 className="text-xl font-semibold">Cart</h2>

        <div className="flex-1 overflow-y-auto rounded-lg border p-2">
          <div className="flex items-center justify-between gap-2 rounded-md border p-2">
            <div className="flex flex-col">
              <span className="font-medium">{item.name}</span>
              <span className="text-sm text-muted-foreground">
                ${item.price.toFixed(2)}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setQuantity((q) => Math.max(0, q - 1))}
              >
                -
              </Button>
              <span className="w-6 text-center">{quantity}</span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setQuantity((q) => q + 1)}
              >
                +
              </Button>
            </div>
          </div>
        </div>

        <div className="flex items-center justify-between text-lg font-semibold">
          <span>Total</span>
          <span>${total.toFixed(2)}</span>
        </div>

        <div className="flex gap-2">
          <Button variant="outline" className="flex-1" onClick={handleClear}>
            Clear
          </Button>
          <Button className="flex-1" onClick={handleCheckout}>
            Check Out
          </Button>
        </div>
      </div>

      {/* Column 2: add buttons that fill the space */}
      <div className="flex flex-1 flex-col gap-2">
        {ADD_BUTTONS.map((label) => (
          <Button key={label} variant="secondary" className="flex-1 text-base">
            Add {label}
          </Button>
        ))}
      </div>

      {/* Column 3: entrees with checkboxes */}
      <Card className="flex-1">
        <CardHeader>
          <CardTitle>Entrees</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-2">
          {ENTREES.map((name) => (
            <Button
              key={name}
              variant="outline"
              className="justify-start gap-3"
              onClick={() => toggleEntree(name)}
            >
              <Checkbox
                checked={selected.includes(name)}
                className="pointer-events-none"
                tabIndex={-1}
              />
              {name}
            </Button>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}

export default Cashier;
