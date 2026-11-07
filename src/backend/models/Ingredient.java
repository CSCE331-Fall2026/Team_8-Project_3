
package models;

import java.sql.Timestamp;

public record Ingredient(
    Integer ingredientId,
    String ingredientName,
    Float quantityInStock,
    StorageType storageType,
    Float ingredientCost,
    Timestamp expiryDate,
    Float minQtyOnHand,
    String measurementUnits,
    Integer shelfLifeDays
) {
    public enum StorageType {freezer, refrigerator, pantry}
}