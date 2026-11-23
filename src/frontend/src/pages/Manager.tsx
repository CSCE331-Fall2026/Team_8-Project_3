import { useEffect, useState } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "../components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import { apiFetch } from "../lib/api";

interface Ingredient {
  ingredientId: number;
  ingredientName: string;
  quantityInStock: number;
  storageType: string;
  ingredientCost: number;
  expiryDate: string;
  minQtyOnHand: number;
  measurementUnits: string;
  shelfLifeDays: number;
}

interface Order {
  orderId: number;
  orderStatus: string;
  createdAt: string;
  updatedAt: string;
  paymentMethod: string;
  orderTotal: number;
}

function Manager() {
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [prices, setPrices] = useState<Record<number, string>>({});
  const [orders, setOrders] = useState<Order[]>([]);
  const [orderCount, setOrderCount] = useState(10);
  const [error, setError] = useState<string | null>(null);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const fetchIngredients = async () => {
    try {
      const res = await apiFetch("/ingredients", {}, { role: "manager" });
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      const data: Ingredient[] = await res.json();
      setIngredients(data);
      const initialPrices: Record<number, string> = {};
      data.forEach((ingredient) => {
        initialPrices[ingredient.ingredientId] = String(ingredient.ingredientCost);
      });
      setPrices(initialPrices);
    } catch (e) {
      console.error("Request failed:", e);
      setError("Failed to load ingredients");
    }
  };

  const fetchOrders = async () => {
    try {
      const res = await apiFetch(`/orders?n=${orderCount}`, {}, { role: "manager" });
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      const data = await res.json();
      setOrders(data);
    } catch (e) {
      console.error("Request failed:", e);
      setError("Failed to load orders");
    }
  };

  useEffect(() => {
    fetchIngredients();
    fetchOrders();
  }, []);

  const refreshAll = async () => {
    setIsRefreshing(true);
    setError(null);
    await Promise.all([fetchIngredients(), fetchOrders()]);
    setIsRefreshing(false);
  };

  const handlePriceChange = (ingredientId: number, value: string) => {
    setPrices((prev) => ({ ...prev, [ingredientId]: value }));
  };

  const handleSavePrice = async (ingredient: Ingredient) => {
    const newCost = Number(prices[ingredient.ingredientId]);
    if (Number.isNaN(newCost)) {
      setError("Invalid price");
      return;
    }
    try {
      const res = await apiFetch(
        `/ingredients/${ingredient.ingredientId}`,
        {
          method: "PUT",
          body: JSON.stringify({ ingredientCost: newCost }),
        },
        { role: "manager" },
      );
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      await fetchIngredients();
    } catch (e) {
      console.error("Request failed:", e);
      setError("Failed to update price");
    }
  };

  const totalSales = orders.reduce((total, order) => total + order.orderTotal, 0);

  return (
    <main className="min-h-full w-full bg-background p-4">
      <header className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Manager</h1>
          <p className="text-sm text-muted-foreground">
            Prices, recent orders, and sales overview
          </p>
        </div>
        <Button variant="outline" onClick={refreshAll} disabled={isRefreshing}>
          <RefreshCw className={isRefreshing ? "animate-spin" : ""} />
          Refresh
        </Button>
      </header>

      {error && (
        <div className="mb-4 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          {error}
        </div>
      )}

      <div className="mb-4 grid gap-4 sm:grid-cols-3">
        <Card>
          <CardContent className="pt-0">
            <p className="text-sm text-muted-foreground">Recent orders</p>
            <p className="mt-1 text-2xl font-semibold">{orders.length}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-0">
            <p className="text-sm text-muted-foreground">Recent sales</p>
            <p className="mt-1 text-2xl font-semibold">${totalSales.toFixed(2)}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-0">
            <p className="text-sm text-muted-foreground">Priced items</p>
            <p className="mt-1 text-2xl font-semibold">{ingredients.length}</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,0.8fr)_minmax(0,1.2fr)]">
        <Card>
          <CardHeader>
            <CardTitle>Item Prices</CardTitle>
            <CardDescription>Update ingredient costs</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-2">
            {ingredients.map((ingredient) => (
              <div
                key={ingredient.ingredientId}
                className="flex items-center gap-3 rounded-lg border p-3"
              >
                <span className="min-w-0 flex-1 truncate font-medium">
                  {ingredient.ingredientName}
                </span>
                <div className="flex items-center gap-1">
                  <span className="text-sm text-muted-foreground">$</span>
                  <input
                    className="h-8 w-24 rounded-lg border bg-background px-2 text-sm outline-none focus:border-ring focus:ring-3 focus:ring-ring/30"
                    type="number"
                    min="0"
                    step="0.01"
                    value={prices[ingredient.ingredientId] ?? ""}
                    onChange={(event) =>
                      handlePriceChange(ingredient.ingredientId, event.target.value)
                    }
                    aria-label={`${ingredient.ingredientName} price`}
                  />
                </div>
                <Button size="sm" onClick={() => handleSavePrice(ingredient)}>
                  Save
                </Button>
              </div>
            ))}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Orders</CardTitle>
            <CardDescription>Review the latest customer orders</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="mb-3 flex flex-wrap items-center gap-2">
              <label htmlFor="order-count" className="text-sm text-muted-foreground">
                Show last
              </label>
              <input
                id="order-count"
                className="h-8 w-16 rounded-lg border bg-background px-2 text-sm outline-none focus:border-ring focus:ring-3 focus:ring-ring/30"
                type="number"
                min="1"
                value={orderCount}
                onChange={(event) => setOrderCount(Number(event.target.value))}
              />
              <span className="text-sm text-muted-foreground">orders</span>
              <Button className="ml-auto" variant="secondary" size="sm" onClick={fetchOrders}>
                Refresh Orders
              </Button>
            </div>

            <div className="grid gap-2">
              {orders.map((order) => (
                <div
                  key={order.orderId}
                  className="grid gap-2 rounded-lg border p-3 sm:grid-cols-[auto_1fr_auto] sm:items-center"
                >
                  <div>
                    <p className="font-medium">#{order.orderId}</p>
                    <p className="text-xs text-muted-foreground">{order.createdAt}</p>
                  </div>
                  <div>
                    <p className="capitalize">{order.orderStatus}</p>
                    <p className="text-xs text-muted-foreground capitalize">
                      {order.paymentMethod.replaceAll("_", " ")}
                    </p>
                  </div>
                  <p className="font-semibold sm:text-right">
                    ${order.orderTotal.toFixed(2)}
                  </p>
                </div>
              ))}
              {orders.length === 0 && (
                <p className="py-8 text-center text-sm text-muted-foreground">
                  No orders to show.
                </p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  );
}

export default Manager;
