import { useState, useEffect } from "react";
import { Button } from "../components/ui/button";
import { Checkbox } from "../components/ui/checkbox";
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
} from "../components/ui/card";
import { API_URL } from "../lib/api";

const ADD_BUTTONS = [
    "Bowl",
    "Plate",
    "Bigger Plate",
    "Single Item",
    "Drink/App",
];

type MenuItem = {
    menuItemId: number;
    menuItemName: string;
    premiumCharge: number;
    isActive: boolean;
    menuItemType: string;
};

type CartItem = {
    id: string; // unique per line
    name: string;
    price: number;
    quantity: number;
    items: string[];
};

// https://team-8-project-3.onrender.com/menu
// LIST OF MENU ITEMS CAN BE FOUND HERE

function Cashier() {
    const [cart, setCart] = useState<CartItem[]>([]);
    const total = cart.reduce(
        (sum, line) => sum + line.price * line.quantity,
        0,
    );
    const [selected, setSelected] = useState<string[]>([]);
    const [menu, setMenu] = useState<MenuItem[]>([]);
    const entrees = menu.filter((m) => m.menuItemType === "ENTREE");
    const sides = menu.filter((m) => m.menuItemType === "SIDE");
    const appetizers = menu.filter((m) => m.menuItemType === "APPETIZER");
    const drink = menu.filter((m) => m.menuItemType === "DRINK");
    useEffect(() => {
      fetch("http://localhost:8080/menu")
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error: ${response.status}`);
          return response.json();
        })
        .then((data: MenuItem[]) => setMenu(data))
        .catch((error) => console.error("Failed to fetch menu:", error));
    }, []);
    console.log(entrees);

    // Usage (inside an async function or top-level module):

    const toggleEntree = (name: string) =>
        setSelected((prev) =>
            prev.includes(name)
                ? prev.filter((n) => n !== name)
                : [...prev, name],
        );

    const handleClear = () => {
        setCart([]);
        setSelected([]);
    };

    const setQuantity = (id: string, change: number) => {
        setCart((prev) =>
            prev.map((line) =>
                line.id === id
                    ? { ...line, quantity: Math.max(0, line.quantity + change) }
                    : line,
            ),
        );
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
                            <Card>
                                {cart.map((item) => (
                                    <div key={item.id}>
                                        <span className="font-medium">
                                            {item.name}
                                        </span>
                                        <span className="text-sm text-muted-foreground">
                                            {item.price}
                                        </span>
                                        <div className="flex items-center gap-2">
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() =>
                                                    setQuantity(item.id, -1)
                                                }
                                            >
                                                -
                                            </Button>
                                            <span className="w-6 text-center">
                                                {item.quantity}
                                            </span>
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() =>
                                                    setQuantity(item.id, 1)
                                                }
                                            >
                                                +
                                            </Button>
                                        </div>
                                    </div>
                                ))}
                            </Card>
                        </div>
                    </div>
                </div>

                <div className="flex items-center justify-between text-lg font-semibold">
                    <span>Total</span>
                    <span>${total.toFixed(2)}</span>
                </div>

                <div className="flex gap-2">
                    <Button
                        variant="outline"
                        className="flex-1"
                        onClick={handleClear}
                    >
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
                    <Button
                        key={label}
                        variant="secondary"
                        className="flex-1 text-base"
                    >
                        Add {label}
                    </Button>
                ))}
            </div>

            {/* Column 3: entrees with checkboxes */}
            <Card className="flex-1 min-h-0 overflow-y-auto">
                <CardHeader>
                    <CardTitle>Entrees</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-2">
                    {entrees.map((item) => (
                        <Button
                            key={item.menuItemId}
                            variant="outline"
                            className="justify-start gap-3"
                            onClick={() => toggleEntree(item.menuItemName)}
                        >
                            <Checkbox
                                checked={selected.includes(item.menuItemName)}
                                className="pointer-events-none"
                                tabIndex={-1}
                            />
                            {item.menuItemName}
                        </Button>
                    ))}
                </CardContent>
                <CardHeader>
                    <CardTitle>Sides</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-2">
                    {sides.map((item) => (
                        <Button
                            key={item.menuItemId}
                            variant="outline"
                            className="justify-start gap-3"
                            onClick={() => toggleEntree(item.menuItemName)}
                        >
                            <Checkbox
                                checked={selected.includes(item.menuItemName)}
                                className="pointer-events-none"
                                tabIndex={-1}
                            />
                            {item.menuItemName}
                        </Button>
                    ))}
                </CardContent>
                <CardHeader>
                    <CardTitle>Appetizers</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-2">
                    {appetizers.map((item) => (
                        <Button
                            key={item.menuItemId}
                            variant="outline"
                            className="justify-start gap-3"
                            onClick={() => toggleEntree(item.menuItemName)}
                        >
                            <Checkbox
                                checked={selected.includes(item.menuItemName)}
                                className="pointer-events-none"
                                tabIndex={-1}
                            />
                            {item.menuItemName}
                        </Button>
                    ))}
                </CardContent>
                <CardHeader>
                    <CardTitle>Drinks</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-2">
                    {drink.map((item) => (
                        <Button
                            key={item.menuItemId}
                            variant="outline"
                            className="justify-start gap-3"
                            onClick={() => toggleEntree(item.menuItemName)}
                        >
                            <Checkbox
                                checked={selected.includes(item.menuItemName)}
                                className="pointer-events-none"
                                tabIndex={-1}
                            />
                            {item.menuItemName}
                        </Button>
                    ))}
                </CardContent>
            </Card>
        </div>
    );
}

export default Cashier;
