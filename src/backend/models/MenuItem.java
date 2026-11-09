package models;

public record MenuItem(
    Integer menuItemId,
    String menuItemName,
    Double premiumCharge,
    Boolean isActive,
    MenuItemType menuItemType) {}
