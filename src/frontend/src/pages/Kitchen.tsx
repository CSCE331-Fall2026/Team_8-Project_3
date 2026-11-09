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

function Kitchen() {
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [error, setError] = useState<string | null>(null);

  const fetchIngredients = async () => {
    try {
      const res = await fetch(`${API_URL}/ingredients`);
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      const data = await res.json();
      setIngredients(data);
    } catch (e) {
      console.error("Request failed:", e);
      setError("Failed to load inventory");
    }
  };

  useEffect(() => {
    fetchIngredients();
  }, []);

  return (
    <section>
      <h1>Kitchen Inventory</h1>
      <button onClick={fetchIngredients}>Refresh</button>
      {error && <p>{error}</p>}
      <ul>
        {ingredients.map((ingredient) => (
          <li key={ingredient.ingredientId}>
            {ingredient.ingredientName} - {ingredient.quantityInStock}{" "}
            {ingredient.measurementUnits} - {ingredient.storageType} - min{" "}
            {ingredient.minQtyOnHand} - expires {ingredient.expiryDate} -{" "}
            {ingredient.shelfLifeDays} day shelf life
          </li>
        ))}
      </ul>
    </section>
  );
}

export default Kitchen;
