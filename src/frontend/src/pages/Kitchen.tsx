import { useEffect, useState } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/card";
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

function Kitchen() {
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchIngredients = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const res = await apiFetch("/ingredients", {}, { role: "kitchen" });
      if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
      const data = await res.json();
      setIngredients(data);
    } catch (e) {
      console.error("Request failed:", e);
      setError("Failed to load inventory");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchIngredients();
  }, []);

  const lowStockCount = ingredients.filter(
    (ingredient) => ingredient.quantityInStock <= ingredient.minQtyOnHand,
  ).length;

  return (
    <main className="min-h-screen w-full bg-background p-4">
      <header className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Kitchen Inventory</h1>
          <p className="text-sm text-muted-foreground">
            Stock levels, storage, and expiration details
          </p>
        </div>
        <Button variant="outline" onClick={fetchIngredients} disabled={isLoading}>
          <RefreshCw className={isLoading ? "animate-spin" : ""} />
          Refresh
        </Button>
      </header>

      {error && (
        <div className="mb-4 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          {error}
        </div>
      )}

      <div className="mb-4 grid gap-4 sm:grid-cols-2">
        <Card>
          <CardContent className="pt-0">
            <p className="text-sm text-muted-foreground">Ingredients</p>
            <p className="mt-1 text-2xl font-semibold">{ingredients.length}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-0">
            <p className="text-sm text-muted-foreground">Low stock</p>
            <p className={`mt-1 text-2xl font-semibold ${lowStockCount ? "text-destructive" : ""}`}>
              {lowStockCount}
            </p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Inventory</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-2">
            {ingredients.map((ingredient) => {
              const isLowStock = ingredient.quantityInStock <= ingredient.minQtyOnHand;

              return (
                <div
                  key={ingredient.ingredientId}
                  className="grid gap-3 rounded-lg border p-3 md:grid-cols-[1.4fr_repeat(4,minmax(0,1fr))] md:items-center"
                >
                  <div>
                    <p className="font-medium">{ingredient.ingredientName}</p>
                    <p className="text-xs text-muted-foreground">{ingredient.storageType}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">In stock</p>
                    <p className={isLowStock ? "font-medium text-destructive" : "font-medium"}>
                      {ingredient.quantityInStock} {ingredient.measurementUnits}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Minimum</p>
                    <p>{ingredient.minQtyOnHand} {ingredient.measurementUnits}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Expires</p>
                    <p>{ingredient.expiryDate}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Shelf life</p>
                    <p>{ingredient.shelfLifeDays} days</p>
                  </div>
                </div>
              );
            })}
            {!isLoading && ingredients.length === 0 && !error && (
              <p className="py-8 text-center text-sm text-muted-foreground">
                No inventory items found.
              </p>
            )}
            {isLoading && ingredients.length === 0 && (
              <p className="py-8 text-center text-sm text-muted-foreground">Loading inventory...</p>
            )}
          </div>
        </CardContent>
      </Card>
    </main>
  );
}

export default Kitchen;
