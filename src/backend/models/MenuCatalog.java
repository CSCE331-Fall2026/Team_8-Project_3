package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** @author Team 8 */
public final class MenuCatalog {

    /** Mirrors the menu_item_type enum in the database. */
    public enum Category {
        SIDE("side"),
        ENTREE("entree"),
        APPETIZER("appetizer"),
        DRINK("drink");

        private final String dbValue;

        /** Associates a category with its database value. */
        Category(String dbValue) {
            this.dbValue = dbValue;
        }

        /** Returns the category matching a database value, or {@code null} if none matches. */
        public static Category fromDb(String value) {
            for (Category category : values()) {
                if (category.dbValue.equalsIgnoreCase(value)) {
                    return category;
                }
            }
            return null;
        }
    }

    public record MenuItem(int id, String name, Category category) {
        /** Queries the database for this item's premium charge (0 if unavailable). */
        public float getPremium(Connection conn) {
            float premium = 0f;
            if (conn == null) {
                return premium;
            }
            try {
                Statement stmt = conn.createStatement();
                String query = "SELECT premium_charge FROM menu_items WHERE menu_item_id = " + id;
                ResultSet result = stmt.executeQuery(query);
                if (result.next()) {
                    premium = result.getFloat("premium_charge");
                }
                result.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error fetching premium for " + name + ":\n" + e.getMessage());
            }
            return premium;
        }
    }

    private static Map<Category, List<MenuItem>> itemsByCategory = emptyCatalog();

    /** Prevents construction of this static catalog utility. */
    private MenuCatalog() {}

    /** Reloads the active menu items from the database. */
    public static void refresh(Connection conn) {
        if (conn == null) {
            return;
        }
        Map<Category, List<MenuItem>> loaded = emptyCatalog();
        try {
            Statement stmt = conn.createStatement();
            String query =
                    "SELECT menu_item_id, menu_item_name, menu_item_type " +
                            "FROM menu_items " +
                            "WHERE active = TRUE " +
                            "ORDER BY menu_item_id";
            ResultSet result = stmt.executeQuery(query);
            while (result.next()) {
                Category category = Category.fromDb(result.getString("menu_item_type"));
                if (category == null) {
                    continue;
                }
                loaded.get(category).add(new MenuItem(
                        result.getInt("menu_item_id"),
                        result.getString("menu_item_name"),
                        category));
            }
            result.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error loading menu items:\n" + e.getMessage());
            return;
        }
        loaded.replaceAll((category, items) -> List.copyOf(items));
        itemsByCategory = loaded;
    }

    /** Returns the menu items currently loaded for a category. */
    public static List<MenuItem> items(Category category) {
        return itemsByCategory.get(category);
    }

    /** Returns the display label for a category. */
    public static String label(Category category) {
        return switch (category) {
            case SIDE -> "Sides";
            case ENTREE -> "Entrees";
            case APPETIZER -> "Appetizers";
            case DRINK -> "Drinks";
        };
    }

    /** Creates an empty item list for each menu category. */
    private static Map<Category, List<MenuItem>> emptyCatalog() {
        Map<Category, List<MenuItem>> catalog = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            catalog.put(category, new ArrayList<>());
        }
        return catalog;
    }
}
