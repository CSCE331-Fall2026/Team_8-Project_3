package repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.Ingredient;
import models.Ingredient.StorageType;

// Stand-in for IngredientRepository when no database is configured; data is lost on restart
public class InMemoryIngredientRepository extends IngredientRepository {
    private final Map<Integer, Ingredient> ingredients = new LinkedHashMap<>();
    private int nextId = 1;

    public InMemoryIngredientRepository() {
        super(null);
        seed("Chicken Thigh", 120.0, StorageType.freezer, 3.25, 30, 40.0, "lb", 90);
        seed("Beef Steak", 60.0, StorageType.freezer, 6.80, 30, 20.0, "lb", 90);
        seed("Shrimp", 45.0, StorageType.freezer, 8.50, 30, 15.0, "lb", 120);
        seed("Broccoli", 35.0, StorageType.refrigerator, 1.40, 5, 10.0, "lb", 7);
        seed("Cabbage", 25.0, StorageType.refrigerator, 0.90, 10, 10.0, "lb", 14);
        seed("Eggs", 180.0, StorageType.refrigerator, 0.25, 14, 60.0, "each", 21);
        seed("Chow Mein Noodles", 80.0, StorageType.pantry, 1.10, 60, 25.0, "lb", 180);
        seed("White Rice", 200.0, StorageType.pantry, 0.60, 120, 50.0, "lb", 365);
        seed("Orange Sauce", 12.0, StorageType.pantry, 9.75, 90, 4.0, "gal", 180);
        seed("Soy Sauce", 10.0, StorageType.pantry, 7.20, 120, 3.0, "gal", 365);
        seed("Egg Roll Wrappers", 300.0, StorageType.refrigerator, 0.08, 20, 100.0, "each", 30);
        seed("Fountain Cups", 500.0, StorageType.pantry, 0.12, 365, 150.0, "each", 730);
    }

    private void seed(String name, double quantity, StorageType storage, double cost, int expiresInDays,
                      double minQuantity, String units, int shelfLifeDays) {
        int id = nextId++;
        String expiry = OffsetDateTime.now().plusDays(expiresInDays).format(InMemoryOrderRepository.TIMESTAMP);
        ingredients.put(id, new Ingredient(id, name, quantity, storage, cost, expiry, minQuantity, units,
                shelfLifeDays));
    }

    @Override
    public synchronized Ingredient createIngredient(Ingredient ingredient) {
        int id = nextId++;
        Ingredient created = new Ingredient(id, ingredient.ingredientName(), ingredient.quantityInStock(),
                ingredient.storageType(), ingredient.ingredientCost(), ingredient.expiryDate(),
                ingredient.minQtyOnHand(), ingredient.measurementUnits(), ingredient.shelfLifeDays());
        ingredients.put(id, created);
        return created;
    }

    @Override
    public synchronized Ingredient getIngredientById(int id) {
        return ingredients.get(id);
    }

    @Override
    public synchronized List<Ingredient> getAllIngredients() {
        return new ArrayList<>(ingredients.values());
    }

    @Override
    public synchronized boolean updateIngredient(int id, Ingredient ingredient) {
        Ingredient current = ingredients.get(id);
        if (current == null) {
            return false;
        }
        ingredients.put(id, new Ingredient(id,
                ingredient.ingredientName() == null ? current.ingredientName() : ingredient.ingredientName(),
                ingredient.quantityInStock() == null ? current.quantityInStock() : ingredient.quantityInStock(),
                ingredient.storageType() == null ? current.storageType() : ingredient.storageType(),
                ingredient.ingredientCost() == null ? current.ingredientCost() : ingredient.ingredientCost(),
                ingredient.expiryDate() == null ? current.expiryDate() : ingredient.expiryDate(),
                ingredient.minQtyOnHand() == null ? current.minQtyOnHand() : ingredient.minQtyOnHand(),
                ingredient.measurementUnits() == null ? current.measurementUnits() : ingredient.measurementUnits(),
                ingredient.shelfLifeDays() == null ? current.shelfLifeDays() : ingredient.shelfLifeDays()));
        return true;
    }

    @Override
    public synchronized boolean deleteIngredient(int id) {
        return ingredients.remove(id) != null;
    }
}
