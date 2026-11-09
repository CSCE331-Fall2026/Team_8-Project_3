import { useEffect, useState } from "react";
import { API_URL } from "../lib/api";

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

  const fetchIngredients = async () => {
    try {
      const res = await fetch(`${API_URL}/ingredients`);
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
      const res = await fetch(`${API_URL}/orders?n=${orderCount}`);
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

  const handlePriceChange = (ingredientId: number, value: string) => {
    setPrices((prev) => ({ ...prev, [ingredientId]: value }));
  };

  const handleSavePrice = (_ingredient: Ingredient) => {
  };

  return (
    <section>
      <h1>Manager</h1>
      {error && <p>{error}</p>}

      <h2>Item Prices</h2>
      <ul>
        {ingredients.map((ingredient) => (
          <li key={ingredient.ingredientId}>
            {ingredient.ingredientName}
            <input
              type="number"
              step="0.01"
              value={prices[ingredient.ingredientId] ?? ""}
              onChange={(e) =>
                handlePriceChange(ingredient.ingredientId, e.target.value)
              }
            />
            <button onClick={() => handleSavePrice(ingredient)}>Save</button>
          </li>
        ))}
      </ul>

      <h2>Orders</h2>
      <label>
        Show last
        <input
          type="number"
          value={orderCount}
          onChange={(e) => setOrderCount(Number(e.target.value))}
        />
        orders
      </label>
      <button onClick={fetchOrders}>Refresh Orders</button>
      <ul>
        {orders.map((order) => (
          <li key={order.orderId}>
            #{order.orderId} - {order.orderStatus} - {order.paymentMethod} - $
            {order.orderTotal} - {order.createdAt} - {order.updatedAt}
          </li>
        ))}
      </ul>
    </section>
  );
}

export default Manager;
