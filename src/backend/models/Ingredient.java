
package models;

public record Ingredient(
    Integer ingredientId,
    String ingredientName,
    Double quantityInStock,
    StorageType storageType,
    Double ingredientCost,
    String expiryDate,
    Double minQtyOnHand,
    String measurementUnits,
    Integer shelfLifeDays
) {
    public enum StorageType {freezer, refrigerator, pantry}
}
