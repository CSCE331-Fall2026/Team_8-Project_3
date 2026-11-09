package repository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.MenuItem;
import models.MenuItemType;

// Stand-in for MenuRepository when no database is configured; data is lost on restart
public class InMemoryMenuRepository extends MenuRepository {
    private final Map<Integer, MenuItem> menuItems = new LinkedHashMap<>();
    private int nextId = 1;

    public InMemoryMenuRepository() {
        super(null);
        seed("Orange Chicken", 0.0, MenuItemType.ENTREE);
        seed("Beijing Beef", 0.0, MenuItemType.ENTREE);
        seed("Broccoli Beef", 0.0, MenuItemType.ENTREE);
        seed("Kung Pao Chicken", 0.0, MenuItemType.ENTREE);
        seed("Honey Walnut Shrimp", 1.5, MenuItemType.ENTREE);
        seed("Black Pepper Angus Steak", 1.5, MenuItemType.ENTREE);
        seed("Chow Mein", 0.0, MenuItemType.SIDE);
        seed("Fried Rice", 0.0, MenuItemType.SIDE);
        seed("White Steamed Rice", 0.0, MenuItemType.SIDE);
        seed("Super Greens", 0.0, MenuItemType.SIDE);
        seed("Chicken Egg Roll", 0.0, MenuItemType.APPETIZER);
        seed("Cream Cheese Rangoon", 0.0, MenuItemType.APPETIZER);
        seed("Fountain Drink", 0.0, MenuItemType.DRINK);
    }

    private void seed(String name, double premiumCharge, MenuItemType type) {
        int id = nextId++;
        menuItems.put(id, new MenuItem(id, name, premiumCharge, true, type));
    }

    @Override
    public synchronized MenuItem insertMenuItem(MenuItem menuItem) {
        int id = nextId++;
        MenuItem created = new MenuItem(id,
                menuItem.menuItemName(),
                menuItem.premiumCharge() == null ? 0.0 : menuItem.premiumCharge(),
                menuItem.isActive() == null ? true : menuItem.isActive(),
                menuItem.menuItemType());
        menuItems.put(id, created);
        return created;
    }

    @Override
    public synchronized MenuItem getMenuItemByID(int id) {
        return menuItems.get(id);
    }

    @Override
    public synchronized List<MenuItem> getMenuItems() {
        return menuItems.values().stream()
                .sorted(Comparator.comparing(MenuItem::menuItemName))
                .toList();
    }

    @Override
    public synchronized boolean updateMenuItem(int id, MenuItem menuItem) {
        MenuItem current = menuItems.get(id);
        if (current == null) {
            return false;
        }
        menuItems.put(id, new MenuItem(id,
                menuItem.menuItemName() == null ? current.menuItemName() : menuItem.menuItemName(),
                menuItem.premiumCharge() == null ? current.premiumCharge() : menuItem.premiumCharge(),
                menuItem.isActive() == null ? current.isActive() : menuItem.isActive(),
                menuItem.menuItemType() == null ? current.menuItemType() : menuItem.menuItemType()));
        return true;
    }

    // Soft delete, like MenuRepository
    @Override
    public synchronized boolean deleteMenuItem(int id) {
        MenuItem current = menuItems.get(id);
        if (current == null) {
            return false;
        }
        menuItems.put(id, new MenuItem(id, current.menuItemName(), current.premiumCharge(), false,
                current.menuItemType()));
        return true;
    }
}
