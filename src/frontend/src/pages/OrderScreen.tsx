import { useState, useEffect } from "react";
import { Button } from "../components/ui/button";
import { Checkbox } from "../components/ui/checkbox";
import { apiFetch } from "../lib/api";
import type { DevRole } from "../lib/firebase";
import { getToken } from "@/lib/firebase";
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
} from "../components/ui/card";

const ADD_BUTTONS = [
    "Bowl",
    "Plate",
    "Bigger Plate",
    "Single Item",
    "Drink/App",
];

const PRICES: Record<string, number> = {
    Bowl: 8.99,
    Plate: 10.99,
    "Bigger Plate": 12.99,
    "Single Item": 4.99,
    "Drink/App": 2.99,
};

const CAPACITY: Record<string, number> = {
    Bowl: 1,
    Plate: 2,
    "Bigger Plate": 3,
};

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

function OrderScreen({ role }: { role: DevRole }) {
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
    const [mode, setMode] = useState<string | null>(null);
    const selectedSideCount = sides.filter((s) =>
        selected.includes(s.menuItemName),
    ).length;
    const sidesFull = selectedSideCount >= 1;
    const selectedEntreeCount = entrees.filter((e) =>
        selected.includes(e.menuItemName),
    ).length;

    const entreeLimit = mode === null ? 0 : (CAPACITY[mode] ?? Infinity);
    const entreesFull = selectedEntreeCount >= entreeLimit;
    useEffect(() => {
        apiFetch("/menu", {}, { auth: false })
            .then((response) => {
                if (!response.ok)
                    throw new Error(`HTTP error: ${response.status}`);
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
        setMode(null);
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

    const addToCart = async () => {
        if (mode === null) return;

        const unitCount =
            mode === "Drink/App" ? Math.max(1, selected.length) : 1;

        const item: CartItem = {
            id: crypto.randomUUID(),
            name: mode,
            price: PRICES[mode] * unitCount,
            quantity: 1,
            items: selected,
        };
        setCart((prev) => [...prev, item]);
        setMode(null);
        setSelected([]);
    };

    const COMBO_IDS: Record<string, number> = {
        Bowl: 1,
        Plate: 2,
        "Bigger Plate": 3,
        "Single Item": 4,
        Appetizer: 9,
        Drink: 5,
    };
    /*

    BOWL(1, "Bowl", 1, 1, 0, 0),
    PLATE(2, "Plate", 1, 2, 0, 0),
    BIGGER_PLATE(3, "Bigger Plate", 1, 3, 0, 0),
    A_LA_CARTE(4, "A La Carte", 0, 0, 0, 0),
    DRINK(5, "Drink", 0, 0, 1, 0),
    BOWL_COMBO(6, "Bowl Combo", 1, 1, 1, 0),
    PLATE_COMBO(7, "Plate Combo", 1, 2, 1, 0),
    BIGGER_PLATE_COMBO(8, "Bigger Plate Combo", 1, 3, 1, 0),
    APPETIZER(9, "Appetizer", 0, 0, 0, 1);

    */

    const handleCheckout = async () => {
        if (cart.length === 0) return;
        try {
            const token = await getToken();
            const res = await apiFetch(
                "/orders",
                {
                    method: "POST",
                    body: JSON.stringify({
                        paymentMethod: "credit_card",
                        lines: cart
                            .filter((line) => line.quantity > 0)
                            .map((line) => ({
                                comboId: COMBO_IDS[line.name],
                                quantity: line.quantity,
                                menuItemIds: line.items.map(
                                    (name) =>
                                        menu.find(
                                            (m) => m.menuItemName === name,
                                        )!.menuItemId,
                                ),
                            })),
                    }),
                },
                { role },
            );
            if (!res.ok) throw new Error(`${res.status} ${await res.text()}`);
            console.log("Order placed:", await res.json());
            setCart([]);
        } catch (e) {
            console.error("Request failed:", e);
        }
    };

    return (
        <div className="flex h-screen w-full flex-row gap-4 p-4">
            {/* Column 1: cart */}
            <div className="flex min-h-0 flex-1 flex-col gap-3">
                <h2 className="text-xl font-semibold">Cart</h2>

                <Card className="flex-1 min-h-0 overflow-y-auto">
                    {cart.length === 0 ? (
                        <p className="p-4 text-sm text-muted-foreground">
                            Cart is empty
                        </p>
                    ) : (
                        cart.map((item) => (
                            <div
                                key={item.id}
                                className="flex items-center justify-between gap-2 border-b p-3 last:border-b-0"
                            >
                                <div className="flex flex-col">
                                    <span className="font-medium">
                                        {item.name}
                                    </span>
                                    {item.items.length > 0 && (
                                        <span className="text-sm text-muted-foreground">
                                            {item.items.join(", ")}
                                        </span>
                                    )}
                                    <span className="text-sm text-muted-foreground">
                                        ${item.price.toFixed(2)}
                                    </span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={() => setQuantity(item.id, -1)}
                                    >
                                        -
                                    </Button>
                                    <span className="w-6 text-center">
                                        {item.quantity}
                                    </span>
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={() => setQuantity(item.id, 1)}
                                    >
                                        +
                                    </Button>
                                </div>
                            </div>
                        ))
                    )}
                </Card>

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
                    <Button
                        className="flex-1"
                        onClick={mode === null ? handleCheckout : addToCart}
                    >
                        {mode === null ? "Check Out" : "Add"}
                    </Button>
                </div>
            </div>

            {/* Column 2: add buttons that fill the space */}
            <div className="flex flex-1 flex-col gap-2">
                {ADD_BUTTONS.map((label) => (
                    <Button
                        key={label}
                        variant={mode === label ? "default" : "secondary"}
                        className="flex-1 text-base"
                        onClick={() => setMode(label)}
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
                            disabled={
                                mode === null ||
                                mode === "Drink/App" ||
                                (entreesFull &&
                                    !selected.includes(item.menuItemName))
                            }
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
                            disabled={
                                mode === null ||
                                mode === "Drink/App" ||
                                (sidesFull &&
                                    !selected.includes(item.menuItemName))
                            }
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
                            disabled={mode !== "Drink/App"}
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
                            disabled={mode !== "Drink/App"}
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

export default OrderScreen;
