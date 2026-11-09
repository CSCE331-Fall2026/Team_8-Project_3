
package models;

public record Ingredient(
    Integer ingredientId,
    String ingredientName,
    Float quantityInStock,
    StorageType storageType,
    Float ingredientCost,
    String expiryDate,
    Float minQtyOnHand,
    String measurementUnits,
    Integer shelfLifeDays
) {
    public enum StorageType {freezer, refrigerator, pantry}
}
